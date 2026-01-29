# Production Deployment Guide for Ledgerly

## Overview
This guide covers deploying Ledgerly to production with Railway (backend) and Vercel (frontend).

## Prerequisites
- Railway account: https://railway.app
- Vercel account: https://vercel.com
- GitHub repository (for deployments)
- PostgreSQL database (Railway can provision this)

## Step 1: Deploy Backend to Railway

### 1.1 Create Railway Project
1. Go to https://railway.app
2. Click "New Project"
3. Select "Deploy from GitHub"
4. Connect your GitHub repository
5. Select the repository

### 1.2 Add PostgreSQL Database
1. In your Railway project, click "New Service"
2. Select "PostgreSQL"
3. Railway will create a `DATABASE_URL` environment variable
4. **Important:** Copy the connection string for backup

### 1.3 Configure Backend Environment Variables
In Railway dashboard, add these variables:

```
JWT_SECRET=<GENERATE_A_STRONG_SECRET>
CORS_ALLOWED_ORIGINS=https://your-frontend-domain.vercel.app
SPRING_PROFILES_ACTIVE=prod
```

**Generate JWT_SECRET:**
```bash
openssl rand -base64 32
```

Example output: `t7Qw9kL2mNpRxFsV8bJ5cD4eH6gU3vW2aZ1qP0tY+Bx=`

### 1.4 Configure Railway Build Settings
1. Go to "Build" settings
2. Verify:
   - **Build Command:** `./gradlew clean build -x test`
   - **Start Command:** `java -Dspring.profiles.active=prod -jar build/libs/app.jar`
   - **Builder:** Nixpacks (auto-detected)

### 1.5 Deploy
1. Railway auto-detects changes on git push
2. Or click "Deploy" button manually
3. Monitor logs to ensure deployment succeeds

**Expected Output:**
```
Started LedgerlyApplication in X.XXX seconds
Server is running on port 8080
```

### 1.6 Get Backend URL
- Go to "Environment" settings
- Find your service
- Public URL will be something like: `https://ledgerly-production-4b76.up.railway.app`
- Your API URL: `https://ledgerly-production-4b76.up.railway.app/api`

## Step 2: Deploy Frontend to Vercel

### 2.1 Connect Repository to Vercel
1. Go to https://vercel.com
2. Click "New Project"
3. Import your GitHub repository
4. Select "Next.js" as framework (auto-detected)
5. **Root Directory:** Set to `./frontend`

### 2.2 Configure Frontend Environment Variables
In Vercel project settings, add:

```
NEXT_PUBLIC_API_BASE_URL=https://your-backend-railway-url/api
```

Example: `NEXT_PUBLIC_API_BASE_URL=https://ledgerly-production-4b76.up.railway.app/api`

### 2.3 Build Settings
Verify auto-detected settings:
- **Framework:** Next.js
- **Build Command:** `next build`
- **Start Command:** `next start`
- **Install Command:** `pnpm install` (or npm install)

### 2.4 Deploy
1. Vercel auto-deploys on git push to main branch
2. Or click "Deploy" button manually
3. Wait for deployment to complete
4. Your frontend URL: `https://ledgerly-xxxxx.vercel.app`

### 2.5 Update Backend CORS
Update the backend CORS configuration with your exact Vercel domain:

**In Railway environment variables:**
```
CORS_ALLOWED_ORIGINS=https://ledgerly-xxxxx.vercel.app
```

## Step 3: Update Backend CORS (Security)

### Option A: Via Environment Variable (Recommended)
```
CORS_ALLOWED_ORIGINS=https://your-vercel-domain.vercel.app
```

### Option B: Via Code (for multiple domains)
Edit `backend/src/main/java/dev/atinroy/ledgerly/config/SecurityConfig.java`:
```java
@Value("${cors.allowed-origins:https://your-domain.vercel.app}")
private String allowedOrigins;
```

## Step 4: Database Migrations (Optional but Recommended)

### Add Flyway for database migrations:

1. Add to `build.gradle.kts`:
```kotlin
implementation("org.flywaydb:flyway-core:9.22.3")
```

2. Create migration file: `backend/src/main/resources/db/migration/V1__Initial_schema.sql`

3. Update `application-prod.properties`:
```
spring.jpa.hibernate.ddl-auto=validate
```

This validates the schema instead of auto-updating it.

## Step 5: Testing Production

### 5.1 Test Backend
```bash
# Health check
curl https://your-backend-railway-url/actuator/health

# Expected response:
{"status":"UP"}
```

### 5.2 Test Frontend
1. Open your Vercel domain: `https://ledgerly-xxxxx.vercel.app`
2. Register a new account
3. Check browser console for logs
4. Verify login works
5. Navigate through pages
6. Check that data loads

### 5.3 Monitor Logs
- **Railway:** Click "Logs" tab to see backend errors
- **Vercel:** Click "Logs" → "Function Logs" to see frontend errors

## Step 6: Custom Domain (Optional)

### Add Custom Domain to Vercel Frontend
1. In Vercel project settings
2. Go to "Domains"
3. Add your custom domain
4. Follow DNS instructions for your registrar

### Add Custom Domain to Railway Backend (Optional)
1. In Railway project settings
2. Add custom domain
3. Update frontend `NEXT_PUBLIC_API_BASE_URL` with new domain

## Environment Variables Checklist

### Backend (Railway)
- [ ] `JWT_SECRET` - Generated strong secret (32+ chars)
- [ ] `DATABASE_URL` - Auto-set by Railway when adding PostgreSQL
- [ ] `CORS_ALLOWED_ORIGINS` - Your Vercel frontend URL
- [ ] `SPRING_PROFILES_ACTIVE=prod` - For production profile

### Frontend (Vercel)
- [ ] `NEXT_PUBLIC_API_BASE_URL` - Your Railway backend API URL

## Security Best Practices

### 1. JWT Secret
- [x] Generate strong random secret: `openssl rand -base64 32`
- [x] Store securely in Railway (never commit to git)
- [x] Rotate periodically in production

### 2. CORS
- [x] Use specific domain instead of wildcards
- [x] Never use `*` in production
- [x] Current config: `https://*.vercel.app` (OK but could be stricter)

### 3. Database
- [x] Railway PostgreSQL is SSL-enabled by default
- [x] Connection pooling configured (max 10 connections)
- [x] Auto-backup enabled in Railway

### 4. Secrets
- [x] Never commit `.env` files with real secrets
- [x] Use Railway/Vercel dashboard for environment variables
- [x] Rotate secrets regularly

## Troubleshooting

### Backend Won't Deploy
```
Error: Build failed
```
- Check Railway logs for specific error
- Verify `JWT_SECRET` is set
- Check `DATABASE_URL` exists
- Ensure Gradle build succeeds locally: `./gradlew clean build`

### Frontend Shows API Errors
```
❌ API Error: 403
```
- Verify `NEXT_PUBLIC_API_BASE_URL` is correct in Vercel
- Check backend CORS includes your Vercel domain
- Clear browser cache and localStorage

### 403 Forbidden on All Requests
- Backend CORS not set to your frontend domain
- Update `CORS_ALLOWED_ORIGINS` in Railway
- Restart backend after changing environment variables

### Database Connection Issues
- Railway PostgreSQL is running (check Railway dashboard)
- `DATABASE_URL` is correct (auto-set by Railway)
- Connection pool not exhausted (monitor in Railway)

## Monitoring & Maintenance

### Backend Health Checks
```bash
# Weekly
curl https://your-backend/actuator/health

# Expected response:
{"status":"UP"}
```

### Database Backups
- Railway includes automatic backups
- Retains 7 days of backups by default
- Can request manual backup in Railway dashboard

### Logs
- **Backend Errors:** Railway logs (real-time)
- **Frontend Errors:** Vercel Function Logs
- **API Errors:** Check browser DevTools Network tab

### Performance Monitoring
- Use Railway dashboard to monitor:
  - CPU usage
  - Memory usage
  - Requests per minute
  - Response times

## Scaling

### When You Need More Resources

#### Backend (Railway)
1. Go to Resource settings
2. Increase CPU/Memory allocation
3. Vertical scaling (upgrade instance)
4. Or use Railway's auto-scaling (contact support)

#### Frontend (Vercel)
- Automatically scales with your usage
- No configuration needed
- Pro plan for higher limits

## Rollback Procedures

### Backend (Railway)
1. Go to "Deployments" tab
2. Find previous successful deployment
3. Click "Redeploy" on that version
4. Confirm deployment

### Frontend (Vercel)
1. Go to "Deployments" tab
2. Find previous successful deployment
3. Click "Promote to Production"
4. Confirm promotion

## Post-Deployment Checklist

- [ ] Backend deploys successfully
- [ ] Frontend deploys successfully
- [ ] Backend health check responds
- [ ] Frontend loads without errors
- [ ] Registration form works
- [ ] Login form works
- [ ] Dashboard loads data
- [ ] All pages accessible
- [ ] Forms submit successfully
- [ ] Logout works
- [ ] Browser console has no errors
- [ ] API calls use correct URL
- [ ] CORS not blocking requests
- [ ] Database is connected
- [ ] Environment variables set correctly
- [ ] Logs show healthy operation

## Useful Commands

```bash
# Check backend health
curl https://your-backend-url/actuator/health

# Check backend info
curl https://your-backend-url/actuator/info

# View Railway logs locally (requires Railway CLI)
railway logs

# Rebuild on Railway
git push origin main  # Auto-triggers rebuild

# View Vercel logs locally (requires Vercel CLI)
vercel logs
```

## Support & Resources

- Railway: https://docs.railway.app
- Vercel: https://vercel.com/docs
- Spring Boot: https://spring.io/projects/spring-boot
- Next.js: https://nextjs.org/docs
- PostgreSQL: https://www.postgresql.org/docs

## Summary

Your production setup:
- **Backend:** Railway (auto-scales, auto-backups)
- **Frontend:** Vercel (CDN, auto-deploys)
- **Database:** PostgreSQL on Railway
- **Environment:** Production Spring Boot profile
- **Security:** JWT authentication, CORS configured, SSL enabled
