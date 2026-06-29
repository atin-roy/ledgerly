# Ledgerly Cleanup Plan

## Phase status

- [x] **Phase A** — Safe cleanup
- [x] **Phase B** — Architecture cleanup
- [x] **Phase C** — Security / data correctness
- [x] **Phase D** — Testing and polish
- [ ] **Phase E** — Resume / demo polish ← current

---

## Phase A — Safe cleanup

No behavior changes. No API contract changes. Zero risk.

- [ ] Remove all debug `console.log` / emoji logs from frontend
- [ ] Delete `frontend/lib/debug.ts` (unused dev utility)
- [ ] Consolidate API base URL: `api.ts` imports from `apiClient.ts` instead of redefining
- [ ] Remove hardcoded Railway URL from `apiClient.ts`; use `NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080/api"`
- [ ] Fix `spring.application.name=finance-backend` → `ledgerly`
- [ ] Add `@Transactional(readOnly = true)` to all read-only service methods
- [ ] Remove unused `id` field from `TransactionUpdateRequest`; fix affected tests

## Phase B — Architecture cleanup

- [ ] Add `MethodArgumentNotValidException` handler to `GlobalExceptionHandler`
- [ ] Add `@Valid` uniformly to controller request bodies
- [ ] Delete `UserController.createUser` (duplicate of auth registration)
- [ ] Protect `UserController.updateUser` / `deleteUser` with owner check
- [ ] Convert `ValidationResult` from mutable record to a plain class

## Phase C — Security / data correctness

- [ ] **Fix IDOR**: enforce authenticated userId in all resource controllers (B-1 in audit)
- [ ] Add Flyway + `V1__init.sql`; set `ddl-auto=validate` in prod
- [ ] Delete `RailwayConfig.java` (VPS uses standard JDBC URL, not `postgres://`)
- [ ] Implement token refresh in `apiClient.ts` (retry on 401 with refresh token)
- [ ] Update CORS defaults (no `*.vercel.app`; explicit domain)
- [ ] Replace `RAILWAY_DEPLOYMENT.md` and `VERCEL_DEPLOYMENT.md` with Caddy + Docker Compose guide

## Phase D — Testing and polish

- [x] Add controller-layer tests verifying IDOR protection
- [x] Add integration test for register → login → refresh → protected endpoint

## Phase E — Resume / demo polish

- [ ] Implement budget spending calculation from transactions
- [ ] Fix budget form to populate categories from API, not hardcoded list
- [ ] Add `categoryId` to `TransactionResponse` (fixes edit form category pre-selection)
- [ ] Add Next.js `<title>` / description metadata to all pages
- [ ] Remove `badgeColor` from frontend `Transaction` type (always same value)

---

## Critical issues (do not demo without fixing)

| ID | Issue | Phase |
|----|-------|-------|
| B-1 | IDOR: any authenticated user can access any other user's data | C |
| H-1 | `UserController` has no authorization | B/C |
| H-2 | `ddl-auto=update` in production | C |
| H-5 | No token refresh — sessions expire in 30 min | C |

## Deployment target

VPS with Caddy reverse proxy + Docker Compose. Not Vercel, not Railway.
- Backend: standard `jdbc:postgresql://` URL via env var
- Frontend: `NEXT_PUBLIC_API_BASE_URL` points to Caddy-proxied backend domain
