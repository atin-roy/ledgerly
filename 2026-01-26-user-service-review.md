# Code Review: UserService

**Date:** 2026-01-26  
**File:** `backend/src/main/java/dev/atinroy/ledgerly/service/UserService.java`  
**Review Context:** Spring Boot + JPA/Hibernate backend service layer

---

## Summary

The `UserService` is currently a **skeleton implementation** with no business logic implemented. While the infrastructure is sound, this represents an incomplete feature. The related ecosystem (entity, repository, exception) shows good practices, but the service layer needs actual methods to fulfill its intended purpose.

---

## Critical Issues (Stop-Ship)

**None identified.** The code as written is not broken—it's simply incomplete.

However, **before shipping**, this service needs to have implemented methods for common user operations (fetch, create, update, etc.). An empty service suggests either:
1. The feature is incomplete
2. Business logic is planned but not written
3. The service was scaffolded but never developed

---

## Major Concerns (Should Fix Before Merge)

### 1. **Service is Empty—No Implemented Methods**

- **Issue:** The `UserService` has zero business logic. It declares a repository dependency but never uses it.
- **Why this matters:**
  - If this is shipped to production, any code calling `UserService` methods will fail at runtime with `NoSuchMethodError`
  - This blocks any feature work that depends on user management (authentication, authorization, profile management, etc.)
  - Signals incomplete work—dangerous in a shared codebase where others may assume the service is functional
- **How to fix:**
  - Implement core user operations based on your domain requirements. At minimum, consider:
    - `User getUserById(Long id)` — fetch user by ID
    - `User getUserByEmail(String email)` — fetch user by email (likely needed for login)
    - `User createUser(UserCreateRequest request)` — register/create a new user
    - `User updateUser(Long id, UserUpdateRequest request)` — update user profile
    - `void deleteUser(Long id)` — soft or hard delete (choose based on your data retention policy)
  - Each method should handle `UserNotFoundException` appropriately when a user is not found
  - Apply `@Transactional` appropriately (read-only for queries, transactional for writes)
- **Learning point:**
  - Services are the **business logic layer**—they orchestrate repository calls, apply validation, handle transactions, and translate between DTOs and entities
  - Never commit empty services; it signals incomplete work and creates confusion about what's implemented vs. planned
  - Consider using TODO markers or `@Deprecated` annotations if a service is truly placeholder work

---

### 2. **Unused Import: `UserNotFoundException`**

- **Issue:** Line 4 imports `UserNotFoundException`, but it's never used in the class.
- **Why this matters:**
  - Dead imports clutter the file and suggest abandoned refactoring
  - They confuse future maintainers about what exceptions are actually thrown
  - In a code review, they signal incomplete cleanup
- **How to fix:**
  - Remove the import: `import dev.atinroy.ledgerly.exception.UserNotFoundException;`
  - Once you implement service methods that can fail to find users, re-add this import
- **Learning point:**
  - Use IDE inspections (IntelliJ: **Analyze → Run Inspections**) to catch unused imports automatically
  - Make it a habit to clean up imports before committing

---

## Suggestions (Nice-to-Haves)

### 1. **Consider Transaction Management Strategy**

- **Suggestion:** When you implement methods, decide on your `@Transactional` boundaries early.
- **Best practice:**
  - Query methods: `@Transactional(readOnly = true)` — improves performance by signaling to Hibernate this is read-only
  - Write methods: `@Transactional` — ensures atomicity and proper flush behavior
  - Example:
    ```java
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Transactional
    public User createUser(UserCreateRequest request) {
        // validation, entity creation, save
        return userRepository.save(user);
    }
    ```
- **Benefit:** Explicit transaction intent prevents hidden bugs and improves query performance

---

### 2. **Plan for Logging**

- **Suggestion:** Once methods are implemented, add structured logging for operational visibility.
- **Example:**
  ```java
  @Transactional
  public User createUser(UserCreateRequest request) {
      log.info("Creating new user with email: {}", request.getEmail());
      // ... creation logic ...
      log.info("User created with id: {}", user.getId());
      return user;
  }
  ```
- **Benefit:** Helps with debugging, monitoring, and audit trails in production

---

### 3. **Input Validation Layer**

- **Suggestion:** Plan for validation before persisting users (e.g., email format, password strength, username uniqueness).
- **Options:**
  - Use Spring's `@Validated` + `@Valid` annotations on request DTOs
  - Implement custom validation in the service if business logic is complex
- **Example:** Before creating a user, validate:
  - Email is not already in the database (check `existsByEmail()`)
  - Password meets strength requirements
  - Username is not reserved/profane
- **Benefit:** Prevents invalid data from entering the database; provides clear error messages to clients

---

## Positive Observations

### 1. **Clean Dependency Injection**
- The use of `@RequiredArgsConstructor` with Lombok eliminates boilerplate and reduces the chance of missing dependency assignments.
- Final field (`private final UserRepository userRepository`) is the Spring best practice for required dependencies.

---

### 2. **Strong Entity & Repository Design**
- The `User` entity has proper constraints:
  - `unique = true, nullable = false` on `username` and `email` prevents duplicate accounts and null values
  - `length` constraints prevent unreasonable values from being persisted
  - Proper use of `@Enumerated(EnumType.STRING)` for the role enum (preserves meaning across migrations)
- The `UserRepository` provides well-designed query methods:
  - `findByEmail()` and `findByUsername()` return `Optional` (idiomatic Java, prevents NPE)
  - `existsByEmail()` is efficient for uniqueness checks (doesn't load full entity)
- Database-level constraints and efficient queries set up the service for success.

---

### 3. **Thoughtful Exception Design**
- `UserNotFoundException` has constructor overloads for both `String email` and `Long userId`, covering common lookup scenarios.
- Custom exception message formatting provides useful context for debugging.

---

### 4. **Good Entity Inheritance Pattern**
- `BaseEntity` uses `@MappedSuperclass` correctly (not a table; just inheritance of common fields).
- Timestamp automation (`@CreationTimestamp`, `@UpdateTimestamp`) is properly applied.
- `@EqualsAndHashCode(onlyExplicitlyIncluded = true)` on base class is the safest approach for JPA entities.

---

## Recommendations for Next Steps

1. **Implement core user service methods immediately** — At minimum: `getUserById()`, `getUserByEmail()`, `createUser()`
2. **Add `@Transactional` annotations** — Mark write methods as transactional, read methods as `readOnly = true`
3. **Remove the unused `UserNotFoundException` import** — Or implement exception handling that uses it
4. **Create integration tests** — Once methods exist, test repository behavior and edge cases
5. **Consider a UserMapper** — I see `UserMapper.java` in your codebase; use it to convert between `User` entity and DTOs for API responses

---

## Learning Summary

- **Services are where business logic lives**, not repositories or controllers
- **Import discipline matters**—unused imports signal incomplete work
- **The surrounding infrastructure (Entity, Repository, Exception) is well-designed**—leverage it with proper service implementations
- **Transaction boundaries should be explicit and intentional** from day one

---

**Status:** ✅ Infrastructure solid | ⏳ Implementation pending
