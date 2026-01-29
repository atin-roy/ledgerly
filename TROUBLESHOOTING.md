# Troubleshooting "Failed to Fetch" Errors

## Quick Diagnosis Steps

### 1. Open Browser Console
Press `F12` or right-click → Inspect → Console tab

Look for these debug messages:
- `🔒 Dashboard Auth Check:` - Shows if you're authenticated
- `🔐 Auth Check:` - Shows token and user info
- `📡 Starting data fetch...` - Shows when API calls start
- `API Request: GET/POST ...` - Shows each API call being made

### 2. Check Authentication

**In the browser console, run:**
```javascript
localStorage.getItem('ledgerly_access_token')
localStorage.getItem('ledgerly_user')
```

**Expected:**
- Access token should be a long string (JWT)
- User should be a JSON object with userId, email, etc.

**If null/missing:**
- You need to login again
- Go to `/login` and sign in

### 3. Check API Connectivity

**In the browser console, run:**
```javascript
fetch('https://ledgerly-production-4b76.up.railway.app/actuator/health')
  .then(r => r.json())
  .then(console.log)
  .catch(console.error)
```

**Expected:** `{"status":"UP"}`

**If error:**
- Backend is down or unreachable
- Check Railway deployment status
- Check if Railway URL is correct

### 4. Check CORS

Look for CORS errors in console like:
```
Access to fetch at '...' from origin '...' has been blocked by CORS policy
```

**If you see CORS errors:**
- Backend CORS configuration needs your Vercel domain
- Set `CORS_ALLOWED_ORIGINS` in Railway to include your Vercel URL

### 5. Check API Requests

Look for red/failed requests in Network tab:
- Press `F12` → Network tab
- Reload the page
- Look for failed requests (red)
- Click on failed request to see details

**Common issues:**
- **401 Unauthorized**: Token expired or invalid → Login again
- **403 Forbidden**: User doesn't have permission
- **404 Not Found**: Wrong API endpoint
- **500 Server Error**: Backend error → Check Railway logs

## Common Issues and Solutions

### Issue 1: "No authentication token found"

**Symptoms:**
- Error message: "No authentication token found"
- Redirected to login immediately

**Solution:**
1. Go to `/login`
2. Login with your credentials
3. Check that login is successful (redirects to /overview)
4. If login fails, check backend logs

### Issue 2: "Failed to fetch" on all pages

**Symptoms:**
- All pages show "Failed to load data"
- Console shows network errors

**Possible Causes:**

**A. Backend is down**
- Check Railway dashboard
- Check if backend is deployed
- Check Railway logs for errors

**B. CORS not configured**
- Backend doesn't allow your Vercel domain
- Set `CORS_ALLOWED_ORIGINS` in Railway
- Example: `https://your-app.vercel.app`

**C. Wrong API URL**
- Check `NEXT_PUBLIC_API_BASE_URL` in Vercel
- Should be: `https://ledgerly-production-4b76.up.railway.app/api`

**D. Network/Firewall issue**
- Try from different network
- Check if Railway is accessible
- Try curl: `curl https://ledgerly-production-4b76.up.railway.app/actuator/health`

### Issue 3: Login works but pages fail

**Symptoms:**
- Can login successfully
- Dashboard pages show "Failed to load"

**Solution:**
1. Check browser console for specific errors
2. Look for the API request URLs being called
3. Verify the URLs are correct
4. Check if token is being sent in headers

**Debug in console:**
```javascript
// Check if token exists
console.log(localStorage.getItem('ledgerly_access_token'))

// Check user ID
const user = JSON.parse(localStorage.getItem('ledgerly_user'))
console.log('User ID:', user.userId)

// Test API call manually
const token = localStorage.getItem('ledgerly_access_token')
const userId = JSON.parse(localStorage.getItem('ledgerly_user')).userId

fetch(`https://ledgerly-production-4b76.up.railway.app/api/users/${userId}/transactions`, {
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
})
.then(r => r.json())
.then(console.log)
.catch(console.error)
```

### Issue 4: CORS errors

**Symptoms:**
- Console shows: "blocked by CORS policy"
- Network tab shows OPTIONS requests failing

**Solution:**
1. Go to Railway dashboard
2. Select your backend service
3. Go to Variables
4. Add/Update: `CORS_ALLOWED_ORIGINS`
5. Value: Your Vercel URL (e.g., `https://your-app.vercel.app`)
6. Redeploy backend

**Or use wildcard (less secure):**
```
CORS_ALLOWED_ORIGINS=https://*.vercel.app
```

### Issue 5: Token expired

**Symptoms:**
- Was working, now shows "Unauthorized"
- 401 errors in console

**Solution:**
1. Logout (or clear localStorage)
2. Login again
3. Token should be refreshed

**Manual fix:**
```javascript
// Clear all auth data
localStorage.removeItem('ledgerly_access_token')
localStorage.removeItem('ledgerly_refresh_token')
localStorage.removeItem('ledgerly_user')

// Then login again
```

## Environment Variables Checklist

### Frontend (Vercel)
```bash
NEXT_PUBLIC_API_BASE_URL=https://ledgerly-production-4b76.up.railway.app/api
```

### Backend (Railway)
```bash
# Required
DATABASE_URL=<auto-provided-by-railway>
JWT_SECRET=<your-secret-key>

# CORS - Add your Vercel domain
CORS_ALLOWED_ORIGINS=https://your-app.vercel.app

# Or use wildcard for all Vercel deployments
CORS_ALLOWED_ORIGINS=https://*.vercel.app,http://localhost:3000
```

## Testing Checklist

- [ ] Backend is deployed and running on Railway
- [ ] Frontend is deployed on Vercel
- [ ] `NEXT_PUBLIC_API_BASE_URL` is set in Vercel
- [ ] `CORS_ALLOWED_ORIGINS` is set in Railway
- [ ] Can access backend health: `https://ledgerly-production-4b76.up.railway.app/actuator/health`
- [ ] Can login successfully
- [ ] Token is stored in localStorage after login
- [ ] Dashboard pages load without errors
- [ ] Can create new items (pots, budgets, bills)

## Debug Commands

### Check Backend Health
```bash
curl https://ledgerly-production-4b76.up.railway.app/actuator/health
```

### Test Login
```bash
curl -X POST https://ledgerly-production-4b76.up.railway.app/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"your@email.com","password":"yourpassword"}'
```

### Test Protected Endpoint
```bash
# Replace TOKEN and USER_ID with your values
curl https://ledgerly-production-4b76.up.railway.app/api/users/USER_ID/transactions \
  -H "Authorization: Bearer TOKEN"
```

## Getting Help

If none of these solutions work:

1. **Check Railway Logs:**
   - Go to Railway dashboard
   - Click on your service
   - Go to "Deployments" → Click latest deployment
   - Check logs for errors

2. **Check Vercel Logs:**
   - Go to Vercel dashboard
   - Click on your project
   - Go to "Deployments" → Click latest deployment
   - Check function logs

3. **Provide Debug Info:**
   - Browser console output
   - Network tab showing failed requests
   - Railway logs
   - Vercel logs
   - Environment variables (without secrets)

## Quick Fixes

### Reset Everything
```javascript
// In browser console
localStorage.clear()
// Then reload and login again
```

### Force Redeploy
1. Railway: Settings → Redeploy
2. Vercel: Deployments → Redeploy

### Check if it's a caching issue
- Try incognito/private browsing mode
- Hard refresh: Ctrl+Shift+R (Windows) or Cmd+Shift+R (Mac)
- Clear browser cache

---

**Last Updated:** After adding debug logging and auth checks
