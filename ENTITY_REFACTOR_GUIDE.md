# Entity Package Refactoring Guide

**Goal:** Fix bugs, add defensive programming, and improve data integrity.

**How to use this:** Read each section. Apply the changes in order. Test after each major section.

---

## SECTION 1: Fix Critical Bugs (Do This First)

### 1.1 Fix Null Pointer Bug in `Budget.java`

**Location:** `Budget.java`, line 23-25

**Problem:** If `budgetAmount` or `budgetSpent` is null, calling `.subtract()` throws `NullPointerException`.

**Why this matters:** Your app crashes when someone queries a budget with missing data.

**The Fix:**

```java
@Transient
public BigDecimal getBudgetRemaining() {
    if (budgetAmount == null || budgetSpent == null) {
        return BigDecimal.ZERO;
    }
    return budgetAmount.subtract(budgetSpent);
}
```

**What changed:** Added null check. Returns `0` if data is missing instead of crashing.

---

### 1.2 Fix Misleading Join Column in `Transaction.java`

**Location:** `Transaction.java`, line 16

**Problem:** Column is named `transaction_type` but it stores an ID (a number), not the type name.

**Why this matters:** Future you will be confused. Database columns storing IDs should end with `_id`.

**The Fix:**

Change this:
```java
@JoinColumn(name = "transaction_type")
```

To this:
```java
@JoinColumn(name = "transaction_type_id")
```

**What changed:** Column name now matches what it stores (an ID).

---

## SECTION 2: Add Missing Database Constraints

**Why this matters:** Without constraints, you can save invalid data (null amounts, duplicate emails). The database should enforce rules, not just your code.

### 2.1 Add Constraints to `User.java`

**Location:** `User.java`, lines 15-17

**Changes:**

```java
@Column(nullable = false, unique = true, length = 50)
private String username;

@Column(nullable = false, unique = true, length = 100)
private String email;

@Column(nullable = false)
private String password; // TODO: Hash with bcrypt before saving
```

**What this does:**
- `nullable = false`: Can't save a user without username/email/password
- `unique = true`: No two users can have same username or email
- `length`: Limits string size in database

---

### 2.2 Add Constraints to `Transaction.java`

**Location:** `Transaction.java`, lines 18-19

**Changes:**

```java
@Column(nullable = false)
private LocalDateTime transactionDate;

@Column(nullable = false, precision = 19, scale = 2)
private BigDecimal transactionAmount;
```

**What this does:**
- `precision = 19, scale = 2`: Stores up to 19 digits, 2 after decimal (e.g., 99999999999999999.99)
- `nullable = false`: Every transaction must have a date and amount

---

### 2.3 Add Constraints to `Bill.java`

**Location:** `Bill.java`, lines 16-18

**Changes:**

```java
@Column(nullable = false, length = 100)
private String billName;

@Column(nullable = false, precision = 19, scale = 2)
private BigDecimal billAmount;

@Column(nullable = false)
private LocalDateTime billDate;
```

---

### 2.4 Add Constraints to `Budget.java`

**Location:** `Budget.java`, lines 14-16

**Changes:**

```java
@Column(nullable = false, length = 100)
private String budgetName;

@Column(nullable = false, precision = 19, scale = 2)
private BigDecimal budgetAmount;

@Column(nullable = false, precision = 19, scale = 2)
private BigDecimal budgetSpent;
```

---

### 2.5 Add Constraints to `Pot.java`

**Location:** `Pot.java`, lines 14-16

**Changes:**

```java
@Column(nullable = false, length = 100)
private String potName;

@Column(nullable = false, precision = 19, scale = 2)
private BigDecimal potTarget;

@Column(nullable = false, precision = 19, scale = 2)
private BigDecimal potSaved;
```

---

### 2.6 Add Constraints to `Party.java`

**Location:** `Party.java`, line 15

**Changes:**

```java
@Column(nullable = false, unique = true, length = 100)
private String partyName;
```

---

### 2.7 Add Constraints to `TransactionType.java`

**Location:** `TransactionType.java`, line 16

**Changes:**

```java
@Column(nullable = false, unique = true, length = 50)
private String transactionTypeName;
```

---

## SECTION 3: Add Audit Fields (Timestamps)

**Why this matters:** You need to know WHEN things were created/modified for debugging and compliance.

### 3.1 Add to `User.java`

**Location:** After line 17 (after `password` field)

**Add these imports at top:**
```java
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
```

**Add these fields:**
```java
@CreationTimestamp
@Column(nullable = false, updatable = false)
private LocalDateTime createdAt;

@UpdateTimestamp
@Column(nullable = false)
private LocalDateTime updatedAt;
```

**What this does:**
- `@CreationTimestamp`: Hibernate automatically sets this when entity is first saved
- `@UpdateTimestamp`: Hibernate updates this every time entity is modified
- `updatable = false` on `createdAt`: Prevents accidental changes to creation time

---

### 3.2 Add to `Transaction.java`

**Location:** After line 22 (after `user` field)

**Add same imports and fields as 3.1**

---

### 3.3 Add to `Bill.java`

**Location:** After line 25 (after `user` field)

**Add same imports and fields as 3.1**

---

### 3.4 Add to `Budget.java`

**Location:** After line 20 (after `user` field, before `@Transient` method)

**Add same imports and fields as 3.1**

---

### 3.5 Add to `Pot.java`

**Location:** After line 19 (after `user` field)

**Add same imports and fields as 3.1**

---

### 3.6 Add to `Party.java`

**Location:** After line 15 (after `partyName` field)

**Add same imports and fields as 3.1**

---

### 3.7 Add to `TransactionType.java`

**Location:** After line 16 (after `transactionTypeName` field)

**Add same imports and fields as 3.1**

---

## SECTION 4: Fix Lombok `@Data` Issue

**Why this matters:** `@Data` generates `equals()` and `hashCode()` using ALL fields. With JPA relationships, this causes infinite loops when entities reference each other.

### 4.1 Replace `@Data` in All Entities

**Apply to:** `User.java`, `Transaction.java`, `Bill.java`, `Budget.java`, `Pot.java`, `Party.java`, `TransactionType.java`

**Change this:**
```java
@Data
@Entity
```

**To this:**
```java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
```

**Add this import:**
```java
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
```

**What this does:** Gives you getters/setters/constructors without the dangerous `equals()` and `hashCode()` that `@Data` provides.

---

## SECTION 5: Clean Up Formatting

### 5.1 Remove Extra Blank Line in `Bill.java`

**Location:** Line 13-14

**Remove the blank line between:**
```java
@GeneratedValue(strategy = GenerationType.IDENTITY)

private Long billId;
```

**Should be:**
```java
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long billId;
```

---

## SECTION 6: Future Refactors (Optional, Not Urgent)

### 6.1 Convert `TransactionType` to Enum

**Why:** Transaction types are fixed (INCOME, EXPENSE, TRANSFER). No need for a database table.

**How:**
1. Create `TransactionTypeEnum.java`:
   ```java
   public enum TransactionTypeEnum {
       INCOME,
       EXPENSE,
       TRANSFER,
       ADJUSTMENT
   }
   ```

2. In `Transaction.java`, replace:
   ```java
   @ManyToOne
   @JoinColumn(name = "transaction_type_id")
   private TransactionType transactionType;
   ```
   
   With:
   ```java
   @Enumerated(EnumType.STRING)
   @Column(nullable = false, length = 20)
   private TransactionTypeEnum transactionType;
   ```

3. Delete `TransactionType.java` entity

**Trade-off:** Harder to add new types at runtime, but you probably don't need that.

---

### 6.2 Document or Remove `Party` Entity

**Problem:** `Party` has no relationships. Why does it exist?

**Options:**
1. If it's for future "split bills with friends" feature, add a comment:
   ```java
   // TODO: Add relationship to Bill for split expenses
   ```
2. If you don't need it yet, delete it

---

## Testing Checklist

After completing sections 1-5:

1. **Run your app** - Does it start without errors?
2. **Check database schema** - Run `SHOW CREATE TABLE transaction;` (or equivalent). Verify columns have correct types and constraints.
3. **Test Budget endpoint** - Call `getBudgetRemaining()` with a budget that has null values. Should return 0, not crash.
4. **Try creating duplicate users** - Should fail with unique constraint violation.
5. **Check timestamps** - Create a new entity. Verify `createdAt` and `updatedAt` are auto-populated.

---

## Summary of Changes

| File | Changes |
|------|---------|
| `Budget.java` | Null check in `getBudgetRemaining()`, constraints, audit fields, Lombok fix |
| `Transaction.java` | Fix join column name, constraints, audit fields, Lombok fix |
| `Bill.java` | Constraints, audit fields, remove blank line, Lombok fix |
| `User.java` | Constraints, audit fields, Lombok fix |
| `Pot.java` | Constraints, audit fields, Lombok fix |
| `Party.java` | Constraints, audit fields, Lombok fix |
| `TransactionType.java` | Constraints, audit fields, Lombok fix |

**Estimated time:** 30-45 minutes if you follow step-by-step.
