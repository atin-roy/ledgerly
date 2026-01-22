# 2026-01-22 Finance Backend Architecture Review

## Summary

This is a **very early-stage learning project** with solid foundational entity modeling and good use of JPA/Hibernate basics. However, there are **critical security and domain design gaps** that would cause problems in any real system—especially around password handling, transaction semantics, and user isolation. The architecture needs rethinking before this becomes production-ready.

---

## Critical Issues (Stop-Ship)

### 1. **Passwords stored as plaintext in the database**

**Issue description**  
User passwords are stored unencrypted in `User.java` line 25:
```
@Column(nullable = false, length = 100)
private String password;
```

**Why this is critical**  
- Any database breach exposes all user credentials immediately
- Violates OWASP, PCI-DSS, GDPR (password handling compliance)
- No amount of HTTPS mitigates this—the vulnerability is at rest
- Length of 100 chars suggests plaintext (hashed passwords are typically 60–72 chars for bcrypt)

**How to fix**  
1. Use `BCryptPasswordEncoder` (or equivalent) in your service layer when saving users
2. Store only the hash: `@Column(nullable = false, length = 72)` (bcrypt output)
3. Never store plaintext passwords; never log them
4. Add a migration strategy for existing plaintext passwords (they should be invalidated or rehashed on next login)

**Learning point**  
Passwords are **special**—they're not normal data. Always assume attackers will eventually see your database. Hash + salt is the bare minimum; consider scrypt or Argon2 for stronger defense.

---

### 2. **No user isolation enforcement—data access control is missing**

**Issue description**  
Repositories use `user_id` foreign keys (e.g., `findByUser_UserId()`), but there's **no service-layer guard** that validates the requesting user owns the data they're querying. The `UserService` has injected repositories but no actual methods.

Example risk from `BudgetRepository`:
```java
Page<Budget> findByUser_UserId(Long userId, Pageable pageable);
```

A malicious client could call `GET /api/budgets?userId=999` and retrieve anyone's budgets if the controller doesn't validate.

**Why this is critical**  
- **Broken object-level authorization** (OWASP A01:2021)
- User A can see/modify User B's data if the controller trusts the `userId` from the request
- In fintech, this is a data breach + regulatory violation

**How to fix**  
1. Add a security context / authentication layer (Spring Security recommended)
2. In **every** service method, validate that `getCurrentUser().getId() == requestedUserId`
3. Never accept a `userId` directly from a request parameter—always extract it from the authenticated session
4. Example pattern:
   ```java
   public Page<Budget> getUserBudgets(Pageable pageable) {
       Long currentUserId = SecurityContextHolder.getContext()
           .getAuthentication().getPrincipal().getId();
       return budgetRepository.findByUser_UserId(currentUserId, pageable);
   }
   ```

**Learning point**  
**Every multi-tenant system has this vulnerability if you don't enforce ownership checks.** This is the #1 way junior systems get hacked. Make it a reflex: "If this data belongs to a user, who verified they own it?"

---

### 3. **Transaction semantics are unclear—no idempotency or race condition protection**

**Issue description**  
Transactions can be created via `TransactionCreateRequest` (line 13–22), but there's no clear semantics:
- No unique constraint or idempotency key to prevent duplicate transactions
- No audit trail or immutability (updatable column is set by default, meaning transactions can be modified)
- If a user submits the same transaction twice (network retry), it will create duplicates

**Why this is critical**  
- In fintech, duplicate transactions = money lost or accounting broken
- Race conditions between create and balance update can corrupt budget/pot values
- Regulatory compliance requires immutable audit trails

**How to fix**  
1. Add an idempotency key to `TransactionCreateRequest` (typically a UUID the client generates)
2. Store it in a new `idempotencyKey` column (unique constraint: `(user_id, idempotencyKey)`)
3. Make `Transaction` immutable (remove `@UpdateTimestamp`, set `updatable = false` on all fields except `updatedAt`)
4. Example check in service:
   ```java
   @Transactional
   public TransactionCreatedResponse createTransaction(
       Long userId, 
       TransactionCreateRequest req) {
       
       Transaction existing = transactionRepository
           .findByUserIdAndIdempotencyKey(userId, req.getIdempotencyKey());
       if (existing != null) return mapToResponse(existing);
       
       // Create new...
   }
   ```

**Learning point**  
**Fintech rule #1: Transactions are immutable events, not mutable entities.** If you make this mistake in production, you'll spend weeks debugging "phantom" balance discrepancies.

---

### 4. **Budget.budgetSpent is mutable—no mechanism to enforce consistency**

**Issue description**  
`Budget.budgetSpent` (line 30 in Budget.java) can be set directly:
```java
@Column(nullable = false, precision = 19, scale = 2)
private BigDecimal budgetSpent;
```

There's no transactional link between creating a transaction and updating the budget. If a transaction is created but the budget update fails, the budget balance becomes wrong.

**Why this is critical**  
- **Data inconsistency**: Budget balance != sum of transactions
- Violates the domain invariant: `budgetAmount - sum(transactions) = budgetRemaining`
- User sees incorrect remaining budget, leading to overspending

**How to fix**  
1. **Never directly set `budgetSpent`.** Make it `private` and provide a `recordSpending(BigDecimal amount)` method
2. In the service layer, use `@Transactional` to wrap the entire operation:
   ```java
   @Transactional
   public void recordTransaction(Long userId, TransactionCreateRequest req) {
       // 1. Create transaction
       Transaction txn = new Transaction(...);
       transactionRepository.save(txn);
       
       // 2. Update budget in same transaction
       Budget budget = budgetRepository.findById(req.getBudgetId());
       budget.recordSpending(req.getAmount());
       budgetRepository.save(budget);
   }
   ```
3. Add a database constraint or periodic validation to ensure consistency

**Learning point**  
**Derived/calculated state (like budget spent) must be computed, not stored.** If you store it, you create two sources of truth. Either compute it on-the-fly (slower but correct) or guard all mutations with transactions.

---

## Major Concerns (Should Fix Before Merge)

### 1. **Ambiguous relationship between UserTransactionType and SystemTransactionType**

**Issue description**  
There are two separate transaction type entities:
- `UserTransactionType` (user-defined categories)
- `SystemTransactionType` (system-wide categories)

But `Transaction.userTransactionType` only references one of them, and it's nullable. The domain logic is unclear: Is a transaction optional-category? Can it fall back to a system type?

**Why this is a concern**  
- Confusing mental model for future maintainers
- Queries might not work as expected if transactions with `null` userTransactionType exist
- No validation ensures a transaction has *at least one* category

**How to fix**  
1. Clarify the domain: Does every transaction need a category?
2. If yes: Add a non-null constraint and decide between user or system types
3. If no: Document the semantics of a null category clearly
4. Option A (cleaner): Combine into a single `TransactionCategory` entity with a `type` enum (USER_DEFINED, SYSTEM_PREDEFINED)
5. Add a comment in the code explaining why this design exists

**Learning point**  
Ambiguous relationships in the domain model leak into bugs and slow down feature development. Nail down the semantics early.

---

### 2. **TransactionCreateRequest validation is incomplete**

**Issue description**  
`TransactionCreateRequest` (lines 13–22):
```java
@NotBlank
private String partyName;  // Must exist and not blank
@NotNull
private String description;  // Can be empty
@NotNull
private BigDecimal amount;  // Can be zero or negative?
@NotNull
private LocalDateTime date;  // Can be in the future?
```

- `partyName` is required, but `Party` is optional in `Transaction` (nullable foreign key). Contradiction.
- `amount` allows zero or negative (no `@Positive` constraint)
- `date` can be in the future (is that valid?)

**Why this is a concern**  
- Invalid transactions slip into the database
- Queries and reports produce nonsensical results (transactions with 0 or -999999 amount)
- Future-dated transactions might mess up budget calculations

**How to fix**  
1. Add proper validation constraints:
   ```java
   @NotBlank
   private String partyName;
   
   @NotBlank  // description shouldn't allow null if it's needed
   private String description;
   
   @DecimalMin("0.01")  // Prevent zero and negative
   private BigDecimal amount;
   
   @PastOrPresent  // or validate in service layer
   private LocalDateTime date;
   ```
2. Add `@Validated` to the controller and `@Valid` to parameters
3. Add custom validators if business rules are more complex

**Learning point**  
Validation at the boundary (DTOs) is your first line of defense. Every missing constraint is a bug waiting to happen.

---

### 3. **Party normalization in @PrePersist/@PreUpdate might mask intentional case variations**

**Issue description**  
In `Party.java` (lines 35–41):
```java
@PrePersist
@PreUpdate
private void normalizeName() {
    if (this.partyName != null) {
        this.partyName = this.partyName.toLowerCase().trim();
    }
}
```

This is **silently modifying user input**. If a user creates "Costco", it becomes "costco". No warning. Later, if they try to create "COSTCO", the unique constraint fires (silently fails or throws a confusing error).

**Why this is a concern**  
- Confusing UX: User expects "Costco" but sees "costco" in responses
- Silent data transformation is a code smell (violates principle of least surprise)
- Queries need to be lowercase-aware everywhere

**How to fix**  
Option A (lighter): Normalize in the service layer and return the normalized value to the client
```java
public PartyCreateResponse createParty(PartyCreateRequest req, Long userId) {
    String normalized = req.getPartyName().toLowerCase().trim();
    // ... check for duplicates with normalized name ...
}
```

Option B (cleaner): Use a database collation (case-insensitive) and document it
```java
@Column(nullable = false, length = 100, columnDefinition = "COLLATE utf8mb4_general_ci")
private String partyName;
```

**Learning point**  
Avoid silent transformations. If you must normalize, do it at the API boundary and confirm to the user what they're getting.

---

### 4. **UserTransactionType also has this normalization issue**

**Issue description**  
Same as Party: `UserTransactionType.java` (lines 35–41) silently lowercases the transaction type name.

**Why this is a concern**  
User creates category "Groceries", it becomes "groceries". Confusing and inconsistent UX.

**How to fix**  
Apply the same fix as Party (either service-layer normalization or database collation).

---

### 5. **Missing indexes for common queries**

**Issue description**  
- `Budget` has an index on `budget_name` (rarely queried by name)
- `Transaction` has an index on `transaction_date` (good)
- **Missing indexes:**
  - `Transaction`: `(user_id, transaction_date)` for range queries
  - `Transaction`: `(user_id, user_transaction_type_id)` for category filtering
  - `Bill`: `(user_id, bill_due_date)` for upcoming bills query
  - `Budget`: `(user_id)` for user's budgets (this is filtered in every list query)
  - `Pot`: `(user_id)` for user's pots

Without these, queries like "Get all budgets for user 123" trigger full table scans.

**Why this is a concern**  
- With 10,000 users × 20 budgets = 200k records, a full table scan takes 100ms+ per request
- At scale, the database becomes a bottleneck

**How to fix**  
Add composite indexes:
```java
@Table(indexes = {
    @Index(name = "idx_budget_user_id", columnList = "user_id"),
    @Index(name = "idx_budget_name", columnList = "budget_name")
})
public class Budget { ... }
```

**Learning point**  
**Always index foreign keys and filter columns used in WHERE clauses.** This is a top-5 performance mistake in early-stage systems.

---

## Suggestions (Nice-to-Haves)

### 1. **Decouple DTOs from Validation Annotations**

Currently, validation annotations live in DTOs. This couples the API contract to business rules.

**Benefit:** If you later need different validation for bulk operations vs. single creates, you'll need separate DTOs anyway. Consider using a separate validation layer:
```java
@Data
public class UserCreateRequest {
    private String email;
    private String username;
    private String password;
}

@Component
public class UserValidator {
    public void validateCreate(UserCreateRequest req) {
        if (req.getEmail() == null || req.getEmail().isBlank()) {
            throw new ValidationException("Email required");
        }
        // ... more rules
    }
}
```

---

### 2. **Bill.bill_date missing from indexes (or is it stored?)**

**Issue description**  
`Bill.java` indexes on `bill_date`, but the column is actually `billDueDate`. Verify this is intentional and rename for clarity.

---

### 3. **SystemTransactionType needs Lombok + timestamps**

**Issue description**  
`SystemTransactionType.java` is missing:
- `@Getter`, `@Setter`, `@NoArgsConstructor`
- Audit columns (`createdAt`, `updatedAt`)

**Benefit:** Consistency with other entities.

---

### 4. **UserService is empty; consider splitting concerns**

**Issue description**  
`UserService` (lines 1–34) has no methods, only constructor dependency injection. It's a placeholder.

**Suggestion:**  
1. Define clear boundaries for what lives in `UserService`:
   - Only user-related operations (create, update, password change)
   - NOT budget/transaction operations (those belong in `BudgetService`, `TransactionService`)
2. Consider a separate `BudgetService`, `TransactionService`, etc. to avoid a God object

---

### 5. **Add global exception handling**

**Issue description**  
There's no `@ControllerAdvice` or global exception handler. If validation fails or a repository throws, the client gets a raw Spring stacktrace.

**Benefit:**  
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handle(ValidationException e) {
        return ResponseEntity.badRequest()
            .body(new ErrorResponse(400, e.getMessage()));
    }
}
```
Cleaner, consistent error responses.

---

### 6. **Pot.targetDate column design**

**Issue description**  
`Pot.targetDate` is nullable (line 30). Is this intentional? Seems like a pot should have a target.

**Suggestion:**  
Clarify: Is the target date optional? If not, make it non-nullable. If yes, consider: What does a null target date mean for business logic?

---

## Positive Observations

### ✅ Good entity naming and structure

- Clear, consistent naming: `userId`, `budgetId`, etc.
- Entities are focused (not God objects)
- Proper use of `@ManyToOne` and foreign keys

### ✅ Timestamps handled well

- Uses `@CreationTimestamp` and `@UpdateTimestamp` from Hibernate
- Immutable `createdAt` (good)
- Helps with auditing

### ✅ DTOs separated from entities

- Request/response DTOs in separate packages
- Not exposing entities directly to API (good foundation, though not yet validated)

### ✅ BigDecimal for money

- Using `BigDecimal` instead of `double` for amounts (correct for fintech)
- Precision = 19, scale = 2 (supports up to 17 digits + 2 decimals, plenty)

### ✅ Repository query methods are well-designed

- Readable method names
- `findByUser_UserId` pattern is clear
- Pagination support is there

### ✅ Composite unique constraints

- `Party` and `UserTransactionType` have composite unique constraints on `(name, user_id)` (good domain modeling)

---

## Summary of Action Items

| Priority | Item | Impact |
|----------|------|--------|
| **Critical** | Hash passwords (do not store plaintext) | Security breach prevention |
| **Critical** | Add user isolation checks in service layer | Prevents unauthorized data access |
| **Critical** | Add idempotency keys to transactions | Prevents duplicate transactions in fintech |
| **Critical** | Make budgets immutable or guarded (no direct mutation of budgetSpent) | Data consistency |
| **Major** | Clarify UserTransactionType vs. SystemTransactionType semantics | Clearer domain model |
| **Major** | Add validation constraints to request DTOs | Prevents invalid data |
| **Major** | Add missing indexes on foreign keys and filter columns | Query performance |
| **Nice** | Decouple validation from DTOs | Flexibility for future rules |
| **Nice** | Add global exception handler | Better error UX |
| **Nice** | Remove silent normalization of party/transaction type names | Principle of least surprise |

---

## Final Notes

This is a solid **learning foundation**. The entity relationships are well-thought, and you're using the ORM correctly. But fintech has no room for guessing—especially around money, user data, and transactions.

Before this becomes production-ready:
1. Implement Spring Security + authentication
2. Enforce user isolation in every service method
3. Make transactions immutable and idempotent
4. Hash passwords
5. Add comprehensive integration tests for multi-step operations (create budget → add transaction → verify balance)

The discipline you apply now will save 10x the debugging later.
