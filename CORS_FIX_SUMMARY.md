# CORS Fix Summary

## Problem
The deployed frontend on Vercel was showing "failed to fetch" errors when trying to register or login. This was caused by CORS (Cross-Origin Resource Sharing) blocking requests from the Vercel domain to the Railway backend.

## Root Cause
The backend's CORS configuration in `WebMvcConfig.java` had a hardcoded placeholder domain (`https://frontend-domain.vercel.app`) that didn't match the actual Vercel deployment URL.

## Solution

### 1. Made CORS Configuration Dynamic
- Changed from hardcoded origins to environment-variable driven configuration
- Added support for wildcard patterns using `allowedOriginPatterns()` instead of `allowedOrigins()`
- This allows patterns like `https://*.vercel.app` to match any Vercel deployment

### 2. Backend Changes

**File: `backend/src/main/java/dev/atinroy/ledgerly/config/WebMvcConfig.java`**
- Added `@Value` injection for `cors.allowed-origins` property
- Changed to use `allowedOriginPatterns()` for wildcard support
- Added support for comma-separated origin list

**File: `backend/src/main/resources/application-prod.properties`**
- Added default CORS configuration: `https://*.vercel.app,http://localhost:3000`
- Can be overridden with `CORS_ALLOWED_ORIGINS` environment variable

### 3. Documentation Added

**File: `DEPLOYMENT.md`** (NEW)
- Complete deployment guide for Railway and Vercel
- Environment variable configuration instructions
- Troubleshooting section for common issues
- Security recommendations

**File: `backend/.env.example`** (NEW)
- Example environment variables for backend
- Different configurations for dev/prod

**File: `frontend/.env.example`** (NEW)
- Example environment variables for frontend
- API URL configuration

**File: `README.md`** (UPDATED)
- Added reference to DEPLOYMENT.md
- Updated production checklist to include CORS

## How to Deploy

### Backend (Railway)

1. **Set Environment Variable** (Optional - defaults work for all Vercel deployments):
   ```
   CORS_ALLOWED_ORIGINS=https://your-specific-app.vercel.app
   ```

2. **Deploy**: Push to GitHub, Railway will auto-deploy

3. **Verify**: 
   ```bash
   curl https://ledgerly-production-4b76.up.railway.app/actuator/health
   ```

### Frontend (Vercel)

1. **Set Environment Variable** (if not already set):
   ```
   NEXT_PUBLIC_API_BASE_URL=https://ledgerly-production-4b76.up.railway.app/api
   ```

2. **Deploy**: Push to GitHub, Vercel will auto-deploy

3. **Test**: Try registering and logging in

## Security Notes

### Default Configuration (Current)
- Allows all Vercel deployments: `https://*.vercel.app`
- Good for development and preview deployments
- Slightly less secure but very convenient

### Recommended for Production
Set specific domain in Railway:
```
CORS_ALLOWED_ORIGINS=https://your-production-app.vercel.app
```

This restricts access to only your production frontend.

## Testing

After deployment, test the following:

1. **Register**: Create a new account on your Vercel deployment
2. **Login**: Login with the created account
3. **Protected Routes**: Navigate to /overview, /transactions, etc.

If you still see "failed to fetch":
1. Check Railway logs for CORS errors
2. Verify `CORS_ALLOWED_ORIGINS` is set correctly
3. Ensure frontend `NEXT_PUBLIC_API_BASE_URL` points to Railway
4. Check browser console for specific error messages

## Files Changed

- `backend/src/main/java/dev/atinroy/ledgerly/config/WebMvcConfig.java`
- `backend/src/main/resources/application.properties`
- `backend/src/main/resources/application-prod.properties`
- `README.md`
- `DEPLOYMENT.md` (new)
- `backend/.env.example` (new)
- `frontend/.env.example` (new)
