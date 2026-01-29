# Quick Start Guide - Testing Ledgerly

## Prerequisites Checklist
- [ ] Node.js installed (`node --version` should show v18+)
- [ ] Java installed (`java -version` should show 17+)
- [ ] PostgreSQL installed or Docker available

## One-Command Startup

### Terminal 1: Start Database
```bash
cd backend
docker-compose up -d  # Or run PostgreSQL manually
```

### Terminal 2: Start Backend
```bash
cd backend
./gradlew bootRun
# Should see: Started LedgerlyApplication in X.XXX seconds
```

### Terminal 3: Start Frontend
```bash
cd frontend
pnpm install  # First time only
pnpm dev
# Should see: ▲ Next.js vX.X.X
# ✓ Ready in XXXms
```

## Test Workflow

### Step 1: Create an Account
1. Open `http://localhost:3000/register`
2. Fill in:
   - Name: `John Doe`
   - Email: `john@example.com`
   - Password: `Password123` (must have uppercase, lowercase, number)
3. Click "Sign Up"
4. Should see success in console and redirect to dashboard

### Step 2: View Dashboard
- You should see:
  - ✅ Balance card
  - ✅ Income card
  - ✅ Expenses card
  - ✅ Recent transactions (empty initially)
  - ✅ Budgets section
  - ✅ Upcoming bills
  - ✅ Pots
  - ✅ Income & expense sources

### Step 3: Add a Transaction
1. Click "New transaction" button
2. Fill in form:
   - Recipient/Sender: `Grocery Store`
   - Amount: `50.00`
   - Category: `Food`
   - Type: `Expense`
3. Click "Save"
4. Transaction should appear in the list

### Step 4: Test Other Pages
1. **Transactions** - Should show all your transactions
2. **Budgets** - Create a new budget
3. **Pots** - Create a savings pot
4. **Bills** - Add an upcoming bill

### Step 5: Logout and Login
1. Long press on Dashboard icon (mobile) or click profile
2. Click "Sign Out"
3. Login with your credentials from Step 1
4. Verify data persists

## Debugging Console

Open browser DevTools (F12) and check:

### Network Tab
- All requests should go to `http://localhost:8080/api`
- Status should be 200 (OK), not 403 (Forbidden)

### Console Tab
Look for successful logs like:
```
🔐 Attempting login to: http://localhost:8080/api/auth/login
✅ Login successful, persisting tokens
✅ Redirecting to dashboard
🔐 Auth Check: { hasToken: true, hasUser: true, user: { userId: 1, email: "john@example.com" } }
🔗 API Request: GET http://localhost:8080/api/users/1/transactions
✅ API Response: [...]
```

### Application Tab
Check localStorage for:
- `ledgerly_access_token` - Should contain JWT
- `ledgerly_refresh_token` - Should contain refresh JWT
- `ledgerly_user` - Should contain user info JSON

## Common Issues & Solutions

### "Failed to fetch" / "Network error"
- ✅ Check backend is running on port 8080
- ✅ Check database is running
- ✅ Look for red errors in console

### "Unauthorized" (401)
- ✅ Clear localStorage and login again
- ✅ Check token in DevTools Application tab

### "Access denied" (403)
- ✅ Verify API URL is `http://localhost:8080/api`
- ✅ Check that `.env.local` exists in frontend folder
- ✅ Restart frontend server after env changes

### "User not found" / "Not authenticated"
- ✅ Clear browser cache and localStorage
- ✅ Register a new account
- ✅ Check browser console for detailed error

## Database Management

### View Database
```bash
# Connect to PostgreSQL
psql -U dev -d finance

# List tables
\dt

# View users
SELECT * FROM users;
SELECT * FROM transactions WHERE user_id = 1;
```

### Reset Database
```bash
docker-compose down -v  # Remove volume
docker-compose up -d    # Recreate
# Backend will auto-create schema on first run
```

## Performance Tips
- Frontend should load instantly (< 1 second)
- API calls should complete in < 100ms
- If slow, check:
  - Database is running
  - Network tab shows requests completing
  - Java heap is not exhausted

## Success Indicators

When everything works correctly, you should see:
1. ✅ No red errors in console
2. ✅ No 403/401 errors in Network tab
3. ✅ Dashboard loads with data
4. ✅ Forms submit successfully
5. ✅ Pagination/filtering works
6. ✅ Login/logout cycle completes

## Next Steps After Testing
- Deploy frontend to Vercel
- Deploy backend to Railway
- Update CORS configuration for production URLs
- Set up proper environment variables on servers
