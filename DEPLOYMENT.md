# Deployment Guide

## Backend Deployment (Railway)

### Required Environment Variables

Set these environment variables in your Railway project:

1. **Database Configuration**
   - `DATABASE_URL` - PostgreSQL connection string (usually auto-provided by Railway)
   - `DATABASE_USERNAME` - Database username
   - `DATABASE_PASSWORD` - Database password

2. **JWT Configuration**
   - `JWT_SECRET` - Secret key for JWT tokens (must be at least 256 bits/32 characters)

3. **CORS Configuration**
   - `CORS_ALLOWED_ORIGINS` - Comma-separated list of allowed frontend origins
   - Example: `https://your-app.vercel.app,https://your-app-staging.vercel.app`
   - Default: `https://*.vercel.app,http://localhost:3000` (allows all Vercel deployments)

4. **Server Configuration**
   - `PORT` - Server port (Railway usually sets this automatically)
   - `SPRING_PROFILES_ACTIVE=prod` - Activates production profile

### Railway Deployment Steps

1. Connect your GitHub repository to Railway
2. Set the root directory to `/backend`
3. Configure environment variables as listed above
4. Railway will automatically detect the Gradle build and deploy

### Verifying Backend Deployment

Test your backend API:
```bash
curl https://ledgerly-production-4b76.up.railway.app/actuator/health
```

## Frontend Deployment (Vercel)

### Required Environment Variables

Set these in your Vercel project settings:

1. **API Configuration**
   - `NEXT_PUBLIC_API_BASE_URL` - Backend API URL
   - Example: `https://ledgerly-production-4b76.up.railway.app/api`

### Vercel Deployment Steps

1. Connect your GitHub repository to Vercel
2. Set the root directory to `/frontend`
3. Configure environment variables as listed above
4. Vercel will automatically detect Next.js and deploy

### Verifying Frontend Deployment

1. Visit your Vercel deployment URL
2. Try to register a new account
3. Try to login with the registered account

## Troubleshooting

### "Failed to fetch" errors

This usually indicates a CORS issue. Check:

1. **Backend CORS Configuration**: Ensure `CORS_ALLOWED_ORIGINS` includes your Vercel domain
2. **Frontend API URL**: Verify `NEXT_PUBLIC_API_BASE_URL` points to the correct Railway backend
3. **HTTPS**: Ensure both frontend and backend use HTTPS in production

### Database Connection Issues

1. Verify `DATABASE_URL` is correctly set in Railway
2. Check Railway logs for connection errors
3. Ensure the database is running and accessible

### JWT Token Issues

1. Verify `JWT_SECRET` is set and is at least 32 characters
2. Check that the secret is the same across all backend instances
3. Clear browser cookies and try again

## Security Notes

### Production CORS Configuration

For maximum security, set `CORS_ALLOWED_ORIGINS` to your specific Vercel domain:

```
CORS_ALLOWED_ORIGINS=https://your-app.vercel.app
```

Instead of using the wildcard pattern `https://*.vercel.app`.

### JWT Secret

Generate a secure JWT secret:

```bash
openssl rand -base64 32
```

Never commit secrets to version control!
