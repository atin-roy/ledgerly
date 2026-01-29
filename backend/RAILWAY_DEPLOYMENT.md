# Railway Deployment Configuration

This directory contains Railway-specific deployment configuration for the Ledgerly backend.

## Files

- `railway.toml` - Railway deployment settings (build and start commands)
- `.env.production.example` - Template for production environment variables

## Quick Start

### 1. Connect Railway to GitHub

1. Go to https://railway.app
2. Create new project
3. Click "Deploy from GitHub"
4. Authorize Railway to access your GitHub
5. Select your Ledgerly repository

### 2. Add PostgreSQL Database

1. In Railway dashboard, click "+ New Service"
2. Select "Database" → "PostgreSQL"
3. Railway automatically creates `DATABASE_URL` environment variable
4. This is all you need - no manual configuration required

### 3. Configure Environment Variables

Go to "Variables" in your Railway project and add:

```
JWT_SECRET=<generate-with-openssl-rand-base64-32>
CORS_ALLOWED_ORIGINS=https://your-vercel-domain.vercel.app
SPRING_PROFILES_ACTIVE=prod
```

See `.env.production.example` for detailed instructions.

### 4. Configure Build Settings (Optional)

The `railway.toml` file already has optimal settings:

```toml
[build]
builder = "nixpacks"
buildCommand = "./gradlew clean build -x test"

[deploy]
startCommand = "java -Dspring.profiles.active=prod -jar build/libs/app.jar"
restartPolicyType = "on-failure"
restartPolicyMaxRetries = 10
```

No changes needed unless you want to use Docker.

### 5. Deploy

Push to main branch:
```bash
git push origin main
```

Railway automatically detects changes and deploys.

Or manually trigger deploy:
1. Go to Railway dashboard
2. Select your backend service
3. Click "Redeploy"

## Environment Variables

### Automatically Provided by Railway
- `DATABASE_URL` - PostgreSQL connection string
- `PORT` - Server port (usually 8080)

### You Must Provide
- `JWT_SECRET` - JWT signing secret (generate: `openssl rand -base64 32`)
- `CORS_ALLOWED_ORIGINS` - Your frontend domain

### Optional
- `SPRING_PROFILES_ACTIVE` - Set to `prod` (already in railway.toml)

## Build Process

Railway uses **Nixpacks** by default, which:
1. Detects Gradle project
2. Installs JDK 21
3. Runs: `./gradlew clean build -x test`
4. Produces: `build/libs/app.jar`

## Deployment Command

On Railway:
```
java -Dspring.profiles.active=prod -jar build/libs/app.jar
```

This:
- Activates production Spring profile
- Uses production configuration (logs, error handling, etc.)
- Loads environment variables (JWT_SECRET, DATABASE_URL, CORS_ALLOWED_ORIGINS)
- Connects to PostgreSQL
- Starts server on port 8080 (or $PORT if set)

## Logs

### View Logs
1. Go to Railway dashboard
2. Select backend service
3. Click "Logs" tab
4. Real-time logs appear

### Common Log Entries

**Successful Start:**
```
Started LedgerlyApplication in 8.234 seconds
Server started successfully on port 8080
```

**Database Connected:**
```
HikariPool-1 - Starting...
HikariPool-1 - Start completed.
```

**Auth Endpoints Ready:**
```
POST /api/auth/login
POST /api/auth/register
```

## Health Checks

### Endpoint
```
GET https://your-railway-url/actuator/health
```

### Response
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP"
    }
  }
}
```

## Monitoring

### Railway Dashboard Shows:
- CPU usage
- Memory usage
- Requests per minute
- Response times
- Error rate
- Network I/O

### Database Monitoring:
- Connection count
- Query performance
- Storage usage
- Backup status

## Troubleshooting

### Build Fails
Check the logs:
```
./gradlew clean build fails
```
- Verify `build.gradle.kts` is correct
- Check Java version compatibility
- Ensure all dependencies are available

### Application Won't Start
```
Failed to start LedgerlyApplication
```
- Check JWT_SECRET is set (will fail if missing)
- Verify DATABASE_URL is correct
- Look for Spring startup errors in logs

### Database Connection Error
```
Failed to connect to database
```
- PostgreSQL service crashed or stopped
- CONNECTION_TIMEOUT exceeded
- Check DATABASE_URL format

### CORS Error in Frontend
```
Preflight request failed (403)
```
- CORS_ALLOWED_ORIGINS doesn't match frontend domain
- Must include `https://` protocol
- No trailing slash after domain

## Scaling

### Increase Resources
1. Go to "Settings" in Railway
2. Click "Pricing/Usage"
3. Choose instance size
4. Railway automatically restarts with new resources

### Database Scaling
1. Click PostgreSQL service
2. Select database
3. Upgrade plan (more storage, CPU, RAM)
4. No downtime migration

## Backups

Railway automatically backs up PostgreSQL:
- **Retention:** 7 days
- **Frequency:** Daily
- **Location:** Automated (managed by Railway)

To manually backup:
1. Go to PostgreSQL service
2. Click "Data"
3. Select database
4. Click "Backup"

## Rollback

To rollback to previous deployment:
1. Go to "Deployments" tab
2. Find successful previous version
3. Click "Redeploy"
4. Railway redeploys that version

## Useful Links

- Railway Docs: https://docs.railway.app
- Spring Boot on Railway: https://docs.railway.app/guides/springboot
- PostgreSQL on Railway: https://docs.railway.app/databases/postgresql
- Railway CLI: https://docs.railway.app/cli/quick-start

## Next Steps

1. ✅ Create Railway project
2. ✅ Add PostgreSQL database
3. ✅ Set environment variables
4. ✅ Deploy backend
5. 📋 Deploy frontend to Vercel
6. 📋 Update frontend API URL
7. 📋 Test production deployment
