# Ledgerly - Code Review & Next Steps

**Review Date:** 2026-01-26
**Project Status:** Early Development
**Overall Readiness:** 5/10

---

## Table of Contents

1. [Executive Summary](#executive-summary)
2. [Architecture Overview](#architecture-overview)
3. [What's Working Well](#whats-working-well)
4. [Critical Issues](#critical-issues)
5. [Code Quality Analysis](#code-quality-analysis)
6. [Security Review](#security-review)
7. [Test Coverage](#test-coverage)
8. [Missing Implementations](#missing-implementations)
9. [Next Steps (Prioritized)](#next-steps-prioritized)
10. [Detailed Recommendations](#detailed-recommendations)

---

## Executive Summary

Ledgerly is a personal finance backend application built with **Spring Boot 4.0.1** and **Java 25**. The project has a solid architectural foundation with clean separation of concerns, but is incomplete. The service layer is well-implemented, but there are **no REST endpoints** exposed yet, **no authentication system**, and **zero test coverage**.

### Scorecard

| Category | Score | Notes |
|----------|-------|-------|
| Architecture | 8/10 | Clean layered design, multi-tenant aware |
| Code Organization | 9/10 | Well-structured packages |
| Entity Design | 7/10 | Good relationships, missing soft delete |
| Service Layer | 7/10 | CRUD complete for core entities |
| Error Handling | 8/10 | Custom exceptions, validation framework |
| API Endpoints | 1/10 | Only a stub controller exists |
| Test Coverage | 0/10 | Single smoke test only |
| Security | 3/10 | Password hashing exists, auth missing |
| Documentation | 6/10 | Good TODO.md, code lacks comments |

---

## Architecture Overview

### Technology Stack

- **Language:** Java 25
- **Framework:** Spring Boot 4.0.1
- **Database:** PostgreSQL 18 (via Docker)
- **ORM:** Hibernate/JPA
- **Build Tool:** Gradle (Kotlin DSL)
- **Mapping:** MapStruct 1.5.5
- **Security:** Spring Security (partial)

### Project Structure

```
backend/src/main/java/dev/atinroy/ledgerly/
├── config/          # SecurityConfig (PasswordEncoder only)
├── controller/      # TestController (stub)
├── dto/
│   ├── request/     # Input DTOs (records)
│   └── response/    # Output DTOs (records)
├── entity/
│   ├── base/        # BaseEntity (audit fields)
│   └── enums/       # UserRole, BillStatus
├── error/           # Custom exceptions, validation framework
├── mapper/          # MapStruct interfaces
├── repository/      # Spring Data JPA repositories
├── service/         # Business logic (User, Transaction, Budget, Pot)
└── validator/       # Custom validators
```

### Data Model

```
User (1) ──┬── (*) Transaction ── Category (*)
           ├── (*) Budget ─────── Category (1)
           ├── (*) Bill
           ├── (*) Pot
           ├── (*) Category
           └── (*) Party
```

---

## What's Working Well

### 1. Clean DTO-Entity Separation
All API contracts use immutable Java records, keeping domain entities isolated:
```java
// Request DTO example
public record TransactionCreateRequest(
    @NotNull BigDecimal amount,
    @NotNull LocalDateTime date,
    @NotNull Long categoryId,
    String partyName
) {}
```

### 2. Multi-Tenant Security at Repository Level
All queries are scoped by user ID, preventing data leakage:
```java
Optional<Budget> findByUser_IdAndId(Long userId, Long budgetId);
List<Transaction> findByUser_IdOrderByDateDesc(Long userId);
```

### 3. Centralized Validation Framework
Custom `ValidationResult` and `ValidationError` types allow structured error reporting:
```java
public record ValidationResult(Set<ValidationError> errors) {
    public static ValidationResult success() { ... }
    public ValidationResult addFieldError(String field, String code, String message) { ... }
}
```

### 4. Well-Designed Exception Hierarchy
Domain-specific exceptions extend a common base:
```
LedgerlyException
├── UserNotFoundException
├── TransactionNotFoundException
├── BudgetNotFoundException
├── CategoryNotFoundException
├── PartyNotFoundException
├── PotNotFoundException
└── BillNotFoundException
```

### 5. Consistent Error Codes
Centralized `ErrorCode` class with categorized constants:
```java
public class ErrorCode {
    // Resource Not Found
    public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
    // Validation
    public static final String FIELD_REQUIRED = "FIELD_REQUIRED";
    public static final String DUPLICATE_ENTRY = "DUPLICATE_ENTRY";
    // Business Logic
    public static final String BUDGET_ALREADY_EXISTS = "BUDGET_ALREADY_EXISTS";
}
```

### 6. Transactional Consistency
All service mutations are wrapped in `@Transactional`:
```java
@Transactional
public void deleteUser(Long userId) { ... }
```

---

## Critical Issues

### 1. CRITICAL: Ledger Immutability Violated

**Location:** `TransactionService.java:47-65`

Transactions should be immutable in a proper ledger system. Currently, amounts can be modified:

```java
// PROBLEM: This violates ledger principles
public TransactionResponse updateTransaction(Long userId, Long transactionId,
                                            TransactionUpdateRequest request) {
    // ... allows changing amount, date, category
    transaction.setAmount(request.amount());  // Should NOT be allowed
}
```

**Impact:** Financial records can be silently altered, breaking audit trails.

**Fix:** Replace updates with reversal transactions (credit/debit pairs).

---

### 2. CRITICAL: No REST API Endpoints

**Location:** `controller/TestController.java`

The entire controller layer is a stub:
```java
@GetMapping("/api/bla")
public String bla() {
    return "Hello World";
}
```

**Impact:** Services exist but are completely inaccessible to clients.

---

### 3. CRITICAL: No Authentication System

**Location:** `config/SecurityConfig.java`

Only a `PasswordEncoder` bean exists. No:
- Login/logout endpoints
- JWT or session management
- SecurityFilterChain configuration
- Role-based access control

```java
// Current config - incomplete
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

**Impact:** Application is unusable in production.

---

### 4. HIGH: Zero Test Coverage

**Location:** `src/test/java/`

Only one test exists:
```java
@Test
void contextLoads() {
    // Empty smoke test
}
```

**Impact:** No confidence in code correctness, refactoring is risky.

---

### 5. HIGH: No Global Exception Handler

**Missing:** `@ControllerAdvice` class to format error responses.

When exceptions occur, clients will receive raw stack traces instead of structured JSON errors.

---

### 6. MEDIUM: Soft Delete Not Implemented

**Location:** `entity/base/BaseEntity.java`

Despite being planned in `TODO.md`, entities use hard deletes:
```java
// Missing from BaseEntity
private LocalDateTime deletedAt;
```

**Impact:** Historical data is permanently lost on deletion.

---

### 7. MEDIUM: Fragile Cascade Deletion

**Location:** `UserService.java:57-65`

Manual cascade deletion is error-prone:
```java
public void deleteUser(Long userId) {
    transactionRepository.deleteAllByUser_Id(userId);
    billRepository.deleteAllByUser_Id(userId);
    budgetRepository.deleteAllByUser_Id(userId);
    // ... 4 more repositories
    userRepository.deleteById(userId);
}
```

**Risk:** If a new entity is added, this method must be updated manually.

---

### 8. MEDIUM: Hardcoded Credentials

**Location:** `application.properties`

```properties
spring.security.user.name=admin
spring.security.user.password=admin
```

**Risk:** If committed to production, this is a security vulnerability.

---

## Code Quality Analysis

### Strengths

1. **Consistent Naming:** Repository methods follow Spring Data conventions
2. **Type Safety:** No raw types, generics used properly
3. **Immutable DTOs:** All DTOs are Java records
4. **Package Organization:** Clear separation by responsibility

### Issues Found

| File | Issue | Severity |
|------|-------|----------|
| `TransactionService.java` | Allows mutation of ledger entries | Critical |
| `UserService.java` | Manual cascade deletion | Medium |
| `SecurityConfig.java` | Incomplete security configuration | High |
| `application.properties` | Hardcoded credentials | Medium |
| `BaseEntity.java` | Missing `deletedAt` for soft delete | Medium |

### Missing Components

| Component | Status |
|-----------|--------|
| REST Controllers | Missing |
| Authentication | Missing |
| Global Exception Handler | Missing |
| Pagination | Designed, not implemented |
| Bill Service | Missing |
| Category Service | Missing |
| Party Service | Missing |

---

## Security Review

### Implemented

| Feature | Status | Location |
|---------|--------|----------|
| Password Hashing | BCrypt | `UserService.java:30` |
| User Scoping | All queries | Repository methods |

### Not Implemented

| Feature | Risk Level |
|---------|------------|
| Authentication endpoints | Critical |
| JWT/Session management | Critical |
| CSRF protection | High |
| Rate limiting | Medium |
| Input sanitization | Medium |
| Security headers | Medium |
| Audit logging | Medium |
| Role-based access | Low (for MVP) |

### Vulnerabilities

1. **Default credentials** in properties file
2. **No authentication** - anyone can call endpoints (once created)
3. **No HTTPS enforcement** configuration
4. **No request validation** at controller level

---

## Test Coverage

### Current State

```
Total Tests: 1
Passing: 1 (context loads)
Coverage: ~0%
```

### What Needs Testing

| Priority | Component | Test Type |
|----------|-----------|-----------|
| High | UserValidator | Unit |
| High | TransactionValidator | Unit |
| High | UserService | Integration |
| High | TransactionService | Integration |
| Medium | All Mappers | Unit |
| Medium | Cascade Deletion | Integration |
| Medium | Controllers (once created) | MockMvc |
| Low | Repositories | DataJpaTest |

---

## Missing Implementations

### Services Not Yet Created

| Entity | Service Status | Controller Status |
|--------|---------------|-------------------|
| User | Complete | Missing |
| Transaction | Complete | Missing |
| Budget | Complete | Missing |
| Pot | Complete | Missing |
| Bill | **Missing** | Missing |
| Category | **Missing** | Missing |
| Party | **Missing** | Missing |

### Infrastructure Gaps

1. **Database Migrations:** No Flyway/Liquibase
2. **API Documentation:** RestDocs configured but unused
3. **Health Checks:** Actuator included but not configured
4. **Logging:** No structured logging setup
5. **Metrics:** No observability configured

---

## Next Steps (Prioritized)

### Phase 1: Core Functionality (Must Have)

| # | Task | Effort | Impact |
|---|------|--------|--------|
| 1 | Create global exception handler (`@ControllerAdvice`) | Low | High |
| 2 | Implement `UserController` with registration endpoint | Medium | Critical |
| 3 | Implement JWT authentication | High | Critical |
| 4 | Create `AuthController` (login/logout) | Medium | Critical |
| 5 | Implement `TransactionController` | Medium | High |
| 6 | Implement `CategoryController` + `CategoryService` | Medium | High |
| 7 | Add unit tests for validators | Low | High |

### Phase 2: Complete CRUD (Should Have)

| # | Task | Effort | Impact |
|---|------|--------|--------|
| 8 | Implement `BudgetController` | Low | Medium |
| 9 | Implement `PotController` | Low | Medium |
| 10 | Implement `BillService` + `BillController` | Medium | Medium |
| 11 | Implement `PartyService` + `PartyController` | Low | Low |
| 12 | Add service integration tests | Medium | High |

### Phase 3: Production Readiness (Nice to Have)

| # | Task | Effort | Impact |
|---|------|--------|--------|
| 13 | Add soft delete to `BaseEntity` | Medium | Medium |
| 14 | Enforce transaction immutability | Medium | High |
| 15 | Add Flyway migrations | Medium | Medium |
| 16 | Configure structured logging | Low | Medium |
| 17 | Add pagination to list endpoints | Low | Medium |
| 18 | Generate API documentation | Low | Medium |

---

## Detailed Recommendations

### 1. Global Exception Handler

Create `GlobalExceptionHandler.java`:

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LedgerlyException.class)
    public ResponseEntity<ErrorResponse> handleLedgerlyException(LedgerlyException ex) {
        ErrorResponse error = new ErrorResponse(
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex) {
        ErrorResponse error = new ErrorResponse(
            "VALIDATION_ERROR",
            ex.getValidationResult().getErrors(),
            LocalDateTime.now()
        );
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleBeanValidation(MethodArgumentNotValidException ex) {
        // Extract field errors from BindingResult
        // Return structured error response
    }
}
```

### 2. Controller Pattern

Follow this pattern for all controllers:

```java
@RestController
@RequestMapping("/api/users/{userId}/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponse> create(
            @PathVariable Long userId,
            @Valid @RequestBody TransactionCreateRequest request) {
        TransactionResponse response = transactionService.createTransaction(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAll(@PathVariable Long userId) {
        return ResponseEntity.ok(transactionService.getTransactions(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getOne(
            @PathVariable Long userId,
            @PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransaction(userId, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> update(
            @PathVariable Long userId,
            @PathVariable Long id,
            @Valid @RequestBody TransactionUpdateRequest request) {
        return ResponseEntity.ok(transactionService.updateTransaction(userId, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long userId,
            @PathVariable Long id) {
        transactionService.deleteTransaction(userId, id);
        return ResponseEntity.noContent().build();
    }
}
```

### 3. JWT Authentication Setup

Add dependencies to `build.gradle.kts`:
```kotlin
implementation("io.jsonwebtoken:jjwt-api:0.12.3")
runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.3")
runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.3")
```

Create `JwtService`, `JwtAuthenticationFilter`, and configure `SecurityFilterChain`.

### 4. Transaction Immutability

Replace `updateTransaction` with reversal logic:

```java
@Transactional
public TransactionResponse correctTransaction(Long userId, Long originalId,
                                              TransactionCorrectionRequest request) {
    Transaction original = getTransactionEntity(userId, originalId);

    // Create reversal transaction
    Transaction reversal = new Transaction();
    reversal.setAmount(original.getAmount().negate());
    reversal.setDate(LocalDateTime.now());
    reversal.setCategory(original.getCategory());
    reversal.setUser(original.getUser());
    reversal.setNote("Reversal of transaction #" + originalId);
    transactionRepository.save(reversal);

    // Create corrected transaction
    Transaction corrected = new Transaction();
    corrected.setAmount(request.amount());
    corrected.setDate(LocalDateTime.now());
    // ... set other fields
    return transactionMapper.toResponse(transactionRepository.save(corrected));
}
```

### 5. Soft Delete Implementation

Update `BaseEntity.java`:
```java
@MappedSuperclass
public abstract class BaseEntity {
    // ... existing fields

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}
```

Add `@Where(clause = "deleted_at IS NULL")` to entity classes.

---

## Summary

Ledgerly has a **solid foundation** with clean architecture and well-designed service layer. However, it's currently **unusable** due to missing:

1. REST API endpoints
2. Authentication system
3. Test coverage

**Immediate priorities:**
1. Global exception handler
2. User registration endpoint
3. JWT authentication
4. Transaction controller
5. Basic test coverage

Once these are in place, the application will be functional for development and testing.

---

*Generated by Claude Code Review*
