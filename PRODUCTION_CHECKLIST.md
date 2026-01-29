# Production Deployment Checklist

Use this checklist to ensure your Ledgerly application is ready for production.

## Pre-Deployment (Week 1)

### Code Quality
- [ ] All tests pass locally: `./gradlew test` (backend), `pnpm test` (frontend)
- [ ] No console errors or warnings in development
- [ ] No security vulnerabilities: `npm audit`, `./gradlew dependencyCheckAnalyze`
- [ ] Code reviewed by team member
- [ ] Documentation updated

### Secrets & Configuration
- [ ] `JWT_SECRET` generated: `openssl rand -base64 32`
- [ ] Database password generated (random, 16+ chars)
- [ ] CORS domain determined (your Vercel domain)
- [ ] All secrets in secure location (NOT in git)
- [ ] `.env` files added to `.gitignore`

### Testing
- [ ] Full end-to-end test locally:
  - [ ] Register new account
  - [ ] Login with credentials
  - [ ] Create transaction
  - [ ] Create budget
  - [ ] Create pot
  - [ ] Create bill
  - [ ] Logout and login again
  - [ ] Test error scenarios

## Infrastructure Setup (Week 1-2)

### Railway Backend
- [ ] Create Railway account
- [ ] Create new Railway project
- [ ] Add PostgreSQL database (Railway auto-configures)
- [ ] Note the DATABASE_URL (auto-created)
- [ ] Generate JWT_SECRET
- [ ] Connect GitHub repository
- [ ] Set root directory: `./backend` (if needed)

### Vercel Frontend
- [ ] Create Vercel account
- [ ] Create new Vercel project
- [ ] Import GitHub repository
- [ ] Set root directory: `frontend`
- [ ] Build settings verified (Next.js auto-detected)

## Environment Variables (Week 2)

### Railway Backend Variables
In Railway dashboard, set:
```
JWT_SECRET=<generated-secret>
CORS_ALLOWED_ORIGINS=https://<your-vercel-domain>.vercel.app
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=<auto-provided-by-railway>
```
- [ ] JWT_SECRET set and saved
- [ ] CORS_ALLOWED_ORIGINS set to Vercel domain
- [ ] SPRING_PROFILES_ACTIVE set to `prod`
- [ ] DATABASE_URL exists (auto-created with PostgreSQL)

### Vercel Frontend Variables
In Vercel dashboard, set:
```
NEXT_PUBLIC_API_BASE_URL=https://<your-railway-url>/api
```
- [ ] NEXT_PUBLIC_API_BASE_URL set to Railway URL
- [ ] Variable saved for all environments (Production, Preview, Development)

## Initial Deployments (Week 2)

### Backend Deployment
- [ ] Push code to GitHub: `git push origin main`
- [ ] Railway detects and builds
- [ ] Build completes successfully (check logs)
- [ ] Application starts: "Started LedgerlyApplication in X.XXX seconds"
- [ ] Get backend URL from Railway dashboard
- [ ] Test health endpoint: `curl https://<backend-url>/actuator/health`
- [ ] Response shows: `"status":"UP"`

### Frontend Deployment
- [ ] Vercel auto-deploys on git push
- [ ] Build completes successfully (check Vercel logs)
- [ ] Deployment status shows green checkmark
- [ ] Frontend loads at `https://<vercel-domain>.vercel.app`

## Post-Deployment Testing (Week 2-3)

### Frontend Accessibility
- [ ] Frontend loads without errors
- [ ] No CORS errors in browser console
- [ ] Browser console shows healthy logs:
  ```
  🔐 Auth Check
  🔗 API Request
  ✅ API Response
  ```

### Authentication Flow
- [ ] Registration page accessible at `/register`
- [ ] Can create new account with:
  - [ ] Name
  - [ ] Email
  - [ ] Valid password (8+ chars, uppercase, lowercase, number)
- [ ] Tokens saved to localStorage after registration
- [ ] Redirects to dashboard `/overview`
- [ ] Login page accessible at `/login`
- [ ] Can login with registered credentials
- [ ] Tokens persist on page reload
- [ ] Logout works and redirects to login
- [ ] Cannot access dashboard without token

### Data Operations
- [ ] Dashboard loads all data:
  - [ ] Balance card displays
  - [ ] Transactions load from API
  - [ ] Budgets load from API
  - [ ] Pots load from API
  - [ ] Bills load from API
- [ ] Create new transaction:
  - [ ] Form submits
  - [ ] API returns 201 Created
  - [ ] Transaction appears in list
- [ ] Update transaction:
  - [ ] Form submits with changes
  - [ ] API returns 200 OK
  - [ ] Changes visible in list
- [ ] Delete transaction:
  - [ ] Confirmation works
  - [ ] API returns 204 No Content
  - [ ] Transaction removed from list
- [ ] Similar tests for budgets, pots, bills

### Error Handling
- [ ] Invalid login shows error message
- [ ] Network timeout shows appropriate error
- [ ] Server error (5xx) shows message
- [ ] Unauthorized (401) redirects to login
- [ ] Forbidden (403) shows access denied message
- [ ] Form validation works (required fields, email format)

### Performance
- [ ] Dashboard loads in < 2 seconds
- [ ] API requests complete in < 500ms
- [ ] No memory leaks (browser task manager)
- [ ] Images optimize and load quickly
- [ ] CSS/JS files cached properly

### Security
- [ ] HTTPS enforced (no HTTP)
- [ ] CORS headers allow only backend domain
- [ ] JWT tokens not exposed in URL
- [ ] localStorage tokens marked secure
- [ ] No sensitive data in console/network tab
- [ ] Security headers present (X-Frame-Options, etc.)

### Browser Compatibility
- [ ] Chrome latest version
- [ ] Firefox latest version
- [ ] Safari latest version
- [ ] Edge latest version
- [ ] Mobile browsers (iOS Safari, Chrome Mobile)

## Database Verification (Week 3)

### Data Integrity
- [ ] Tables created correctly
- [ ] User record created on registration
- [ ] Transaction records created properly
- [ ] Foreign keys configured
- [ ] Indexes created for performance

### Backup Configuration
- [ ] Railway PostgreSQL backups enabled
- [ ] Backup retention: 7 days
- [ ] Manual backup capability confirmed
- [ ] Backup restore tested (optional)

## Monitoring & Alerts (Week 3)

### Backend Monitoring
- [ ] Railway dashboard accessible
- [ ] Can view CPU/Memory/Disk usage
- [ ] Can view logs in real-time
- [ ] Health check endpoint working
- [ ] Error logs being recorded

### Frontend Monitoring
- [ ] Vercel analytics accessible
- [ ] Can view Web Vitals (LCP, FID, CLS)
- [ ] Can view error logs
- [ ] Deployment history accessible

### Log Monitoring
- [ ] Application logs capture important events
- [ ] Error logs are searchable
- [ ] Can identify auth failures
- [ ] Can identify database issues

## Custom Domain (Optional - Week 4)

### Domain Configuration
- [ ] Custom domain registered
- [ ] DNS records configured for Vercel
- [ ] Custom domain assigned in Vercel
- [ ] HTTPS certificate auto-provisioned
- [ ] Domain redirects correctly

### Backend Subdomain (Optional)
- [ ] API subdomain configured (e.g., api.yourdomain.com)
- [ ] CORS updated to custom domain
- [ ] Tests pass with custom domain

## Documentation (Week 4)

### Internal Documentation
- [ ] README.md updated with prod setup
- [ ] API documentation current
- [ ] Database schema documented
- [ ] Deployment procedures documented
- [ ] Troubleshooting guide created

### User Documentation
- [ ] User guide created
- [ ] FAQ documented
- [ ] Contact/support information added
- [ ] Privacy policy updated

## Final Verification (Week 4)

### End-to-End Test
- [ ] Register new account
- [ ] Complete full user workflow:
  - [ ] Add transactions
  - [ ] Create budgets
  - [ ] Create pots
  - [ ] Add bills
  - [ ] Check categories
- [ ] Test all pages accessible
- [ ] Test all forms work
- [ ] Test error scenarios
- [ ] Test logout/login cycle

### Performance Validation
- [ ] All pages load quickly
- [ ] API responses < 500ms
- [ ] No 404 errors
- [ ] No CORS errors
- [ ] No JavaScript errors

### Security Audit
- [ ] OWASP Top 10 reviewed
- [ ] SQL injection tested (not vulnerable)
- [ ] XSS tested (not vulnerable)
- [ ] CSRF protection verified
- [ ] Authentication working correctly

## Go-Live Checklist (Day 1)

### Final Checks
- [ ] Database backups verified
- [ ] Monitoring alerts configured
- [ ] Team trained on deployment
- [ ] Rollback procedure documented
- [ ] Support plan in place

### Go-Live Communication
- [ ] Users notified of launch
- [ ] Support team briefed
- [ ] Status page created (if applicable)
- [ ] Incident response plan ready

### Post-Launch Monitoring (First Week)
- [ ] Monitor error rates
- [ ] Check performance metrics
- [ ] Verify user signups
- [ ] Monitor database usage
- [ ] Check support tickets
- [ ] Collect user feedback

## Post-Launch (Ongoing)

### Regular Maintenance
- [ ] Weekly: Check logs for errors
- [ ] Weekly: Verify backups running
- [ ] Monthly: Review performance metrics
- [ ] Monthly: Check for security updates
- [ ] Quarterly: Security audit

### Scaling & Optimization
- [ ] Monitor CPU/Memory usage
- [ ] Add caching if needed
- [ ] Optimize slow queries
- [ ] Scale infrastructure if traffic increases
- [ ] Review and optimize costs

### Updates & Patches
- [ ] Apply security patches immediately
- [ ] Update dependencies monthly
- [ ] Test updates in staging first
- [ ] Schedule maintenance windows
- [ ] Plan rollback strategy

## Troubleshooting Guide

### Common Issues

#### Backend Won't Start
- [ ] Check JWT_SECRET is set
- [ ] Check DATABASE_URL exists
- [ ] Check logs for detailed error
- [ ] Verify database is running

#### Frontend Can't Connect
- [ ] Check NEXT_PUBLIC_API_BASE_URL is correct
- [ ] Check CORS_ALLOWED_ORIGINS includes frontend domain
- [ ] Check backend is running
- [ ] Check network connectivity

#### Database Connection Fails
- [ ] Check DATABASE_URL format
- [ ] Check PostgreSQL is running
- [ ] Check connection pool settings
- [ ] Check firewall rules

#### Slow Performance
- [ ] Check CPU/Memory usage
- [ ] Review slow queries
- [ ] Check database indexes
- [ ] Consider caching
- [ ] Scale infrastructure

## Success Indicators

When everything works correctly, you should see:
- ✅ No red errors in logs
- ✅ Dashboard loads with data
- ✅ Forms submit successfully
- ✅ Users can register and login
- ✅ All pages accessible
- ✅ API responses under 500ms
- ✅ HTTPS on all pages
- ✅ Mobile responsive
- ✅ Error handling works
- ✅ Security headers present

## Sign-Off

- [ ] Dev lead approves deployment
- [ ] Product owner approves deployment
- [ ] Security review completed
- [ ] Performance testing passed
- [ ] Load testing completed (if applicable)

---

**Deployment Date:** ___________  
**Deployed By:** ___________  
**Verified By:** ___________  
**Go-Live Date:** ___________
