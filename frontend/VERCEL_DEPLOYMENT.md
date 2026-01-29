# Vercel Deployment Configuration

This directory contains Vercel-specific deployment configuration for the Ledgerly frontend.

## Files

- `vercel.json` - Vercel project configuration (build, redirects, headers)
- `.env.production.example` - Template for production environment variables

## Quick Start

### 1. Connect Vercel to GitHub

1. Go to https://vercel.com
2. Click "Add New..." → "Project"
3. Click "Import Git Repository"
4. Authorize Vercel to access your GitHub
5. Select your Ledgerly repository

### 2. Configure Root Directory

During import:
- Click "Edit" next to "ROOT DIRECTORY"
- Set to: `frontend`
- This tells Vercel where your Next.js app is

### 3. Configure Environment Variables

Before deploying:
1. Go to "Settings" → "Environment Variables"
2. Add:
   ```
   NEXT_PUBLIC_API_BASE_URL=https://your-railway-backend-url/api
   ```
3. Select environments: Production, Preview, Development
4. Click "Save"

Replace `your-railway-backend-url` with your actual Railway URL.

### 4. Deploy

Vercel automatically deploys when you push to main:
```bash
git push origin main
```

Or manually trigger:
1. Click "Deploy" button in Vercel dashboard
2. Vercel rebuilds and deploys latest code

## Build Configuration

The `vercel.json` file configures:

### Build Command
```
pnpm build
```
Runs Next.js build in the frontend directory.

### Install Command
```
pnpm install
```
Uses pnpm for fast dependency installation.

### Output Directory
```
.next
```
Where Next.js outputs compiled application.

## Environment Variables

### Automatically Provided by Vercel
- `NODE_ENV` - Set to `production`
- `VERCEL` - Set to `true`
- `VERCEL_ENV` - Set to `production`

### You Must Provide
- `NEXT_PUBLIC_API_BASE_URL` - Your Railway backend API URL

### Setting Environment Variables

1. Go to https://vercel.com/your-project
2. Click "Settings"
3. Click "Environment Variables"
4. Add key/value pair:
   - Key: `NEXT_PUBLIC_API_BASE_URL`
   - Value: `https://your-railway-url/api`
5. Select which environments to apply to
6. Click "Save"

### Note About NEXT_PUBLIC_
- Variables starting with `NEXT_PUBLIC_` are visible in browser
- This is OK - they're just URLs
- DO NOT put secrets in NEXT_PUBLIC_ variables
- Only API URLs and public config

## Security Headers

The `vercel.json` automatically adds security headers:

```json
{
  "X-Content-Type-Options": "nosniff",      // Prevent MIME sniffing
  "X-Frame-Options": "DENY",                 // Prevent clickjacking
  "X-XSS-Protection": "1; mode=block",      // Enable XSS protection
  "Referrer-Policy": "strict-origin-when-cross-origin"  // Limit referrer info
}
```

## Redirects

Automatic redirect from root to dashboard:
```
/ → /overview (status 307)
```

Users accessing your domain go directly to dashboard.

## Logs

### View Logs

#### Function Logs (Backend API)
1. Go to Vercel dashboard
2. Select project
3. Click "Functions" tab
4. See API route logs

#### Build Logs
1. Go to "Deployments"
2. Click a deployment
3. Click "Runtime logs"
4. See build output

### Common Log Messages

**Successful Build:**
```
Analyzing source code...
Running "pnpm build"
Compiled successfully
Generated `.next` folder
Ready to deploy
```

**Deployment Complete:**
```
✓ Deployment successful
✓ Production domain ready
✓ CSR (Client-side Rendering) enabled
```

## Monitoring

### Performance
Vercel provides analytics:
1. Go to "Analytics"
2. View real-time metrics:
   - Requests per minute
   - Response times
   - Error rate
   - Bandwidth usage

### Logs
View edge function logs:
1. Go to "Functions"
2. Filter by time/status
3. Search for errors

## Troubleshooting

### Build Fails
```
Build failed: Command failed
```
Check build logs:
1. Go to "Deployments"
2. Click failed deployment
3. View "Build Logs"
4. Common issues:
   - Missing environment variables
   - Dependency installation failed
   - Syntax errors in code

### Environment Variable Not Found
```
Error: NEXT_PUBLIC_API_BASE_URL is undefined
```
- Check variable is set in "Settings" → "Environment Variables"
- Verify it's set for the correct environment
- Redeploy after setting (new builds pick up env vars)
- Note: Must start with `NEXT_PUBLIC_` to be accessible in browser

### API Requests Fail (404, CORS, etc.)
```
❌ API Error: 403
```
Check browser console:
1. Open DevTools (F12)
2. Go to "Console"
3. Look for log lines with API URL
4. Verify URL matches Railway backend
5. Check Network tab for actual request

### Frontend Shows 404
- Ensure root directory is set to `frontend`
- Check that `next.config.ts` exists
- Verify `package.json` is in `frontend/` directory
- Check build command in `vercel.json`

## Deployment Process

1. **Code Push**
   ```bash
   git push origin main
   ```

2. **Vercel Detects Change**
   - Automatically triggers build

3. **Build Phase**
   - Installs dependencies: `pnpm install`
   - Builds Next.js: `pnpm build`
   - Generates `.next` folder

4. **Deploy Phase**
   - Uploads assets to Vercel CDN
   - Updates production domain
   - Activates new deployment

5. **Preview & Production**
   - Preview URL: `https://ledgerly-pr-123.vercel.app`
   - Production URL: `https://ledgerly-xxxxx.vercel.app`

## Custom Domain

### Add Custom Domain
1. Go to "Settings" → "Domains"
2. Click "Add Domain"
3. Enter your domain (e.g., `ledgerly.com`)
4. Follow DNS instructions
5. Wait for DNS propagation (5-48 hours)

### Update Backend CORS
After setting custom domain, update backend:

**In Railway environment variables:**
```
CORS_ALLOWED_ORIGINS=https://ledgerly.com
```

Restart backend after changing CORS.

## Rollback

### Rollback to Previous Deployment
1. Go to "Deployments"
2. Find previous successful deployment
3. Click "⋮" (three dots)
4. Select "Promote to Production"
5. Confirm rollback

### Instant Rollback
1. Most recent deployments stay staged
2. Quick rollback to any recent version
3. No build required for rollback

## Environment Variable Best Practices

### DO:
- ✅ Use `NEXT_PUBLIC_` for public URLs
- ✅ Set environment-specific values (prod, preview, dev)
- ✅ Rotate secrets periodically
- ✅ Use separate values per environment

### DON'T:
- ❌ Commit environment variables to git
- ❌ Put secrets in `NEXT_PUBLIC_` variables
- ❌ Use same values across all environments
- ❌ Hardcode API URLs in code

## Testing Deployment

### 1. After Deployment
- Wait for green checkmark on Vercel
- Click deployment to view it
- Check URL matches your domain

### 2. Test Frontend
```bash
# Open in browser
https://your-vercel-domain.vercel.app

# Or with custom domain
https://your-custom-domain.com
```

### 3. Test API Calls
1. Open browser DevTools (F12)
2. Go to "Console"
3. Check for logs:
   ```
   🔗 API Request: https://your-railway-url/api/users/1/transactions
   ```
4. Verify API URL is correct

### 4. Verify Authentication
1. Register a new account
2. Check console for logs
3. Should redirect to dashboard
4. Verify data loads

### 5. Check All Pages
- ✅ Login page
- ✅ Register page
- ✅ Dashboard
- ✅ Transactions
- ✅ Budgets
- ✅ Pots
- ✅ Bills

## Performance Optimization

### Caching
Vercel automatically caches:
- Static assets (images, CSS, JS)
- Generated pages
- API responses (if configured)

### CDN
Vercel uses global CDN:
- 99+ edge locations worldwide
- Automatic geo-routing
- Sub-100ms response times globally

### Analytics
Monitor performance:
1. Go to "Analytics" → "Web Vitals"
2. Track:
   - LCP (Largest Contentful Paint)
   - FID (First Input Delay)
   - CLS (Cumulative Layout Shift)

## Useful Links

- Vercel Docs: https://vercel.com/docs
- Next.js Docs: https://nextjs.org/docs
- Vercel Next.js Guide: https://vercel.com/docs/frameworks/nextjs
- Environment Variables: https://vercel.com/docs/concepts/projects/environment-variables

## Next Steps

1. ✅ Create Vercel project
2. ✅ Set root directory to `frontend`
3. ✅ Set environment variables
4. ✅ Deploy frontend
5. 📋 Update backend CORS
6. 📋 Test production deployment
7. 📋 Add custom domain (optional)
