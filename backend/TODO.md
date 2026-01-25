# TODO

---

## 1. Persistence & Base Architecture

- [x] Create `BaseEntity` in `entity/base`
    - `id`
    - `createdAt`
    - `updatedAt`
    - `deletedAt` (soft delete)

- [x] Extend all domain entities from `BaseEntity`
    - User
    - Transaction
    - Budget
    - Bill
    - Pot
    - Party

- [x] Remove duplicated audit fields from entities
    - `createdAt`
    - `updatedAt`
    - prefixed `*Id` fields

---

## 2. Naming & Domain Cleanup

- [>] Replace `userId`, `transactionId`, etc. inside entities with:
    - `id`
    - object references (`User user`, `Party party`, etc.)

- [ ] Ensure DTOs remain explicit
    - `userId`, `transactionId` allowed **only in DTOs**

- [ ] Align repository method names with object navigation
    - `findByUser_Id(...)`
    - `findByBudget_Id(...)`

---

## 3. Ledger Rules (Critical)

- [ ] Enforce immutability for `Transaction.amount`
    - No updates after creation
    - Guard at service layer

- [ ] Replace transaction updates with **reversal transactions**
    - Original transaction remains untouched
    - Reversal negates original amount
    - New transaction records corrected value

- [ ] Decide and document:
    - editable grace window (if any)
    - or strict immutability

---

## 4. Soft Delete Strategy

- [ ] Use `deletedAt` instead of hard deletes
- [ ] Do NOT hard-delete ledger entities
- [ ] Decide per-entity visibility rules
    - user-facing queries hide deleted rows
    - audit queries can access all

- [ ] Add `@Where(deleted_at IS NULL)` selectively
    - only after query patterns stabilize

---

## 5. Transaction Semantics

- [ ] Clarify transaction direction rules
    - income vs expense
    - sign conventions (+ / -)

- [ ] Decide whether `Bill` auto-generates `Transaction`
- [ ] Decide whether `Pot` is:
    - mutable state
    - or event-derived balance

---

## 6. Future (Not Now)

- [ ] Add optimistic locking where needed (`@Version`)
- [ ] Add audit metadata
    - `deletedBy`
    - `deletionReason`
- [ ] Introduce read-only audit/reporting views

---

## Notes

- Ledger truth > CRUD convenience
- History is append-only
- Corrections are new facts, not edits
