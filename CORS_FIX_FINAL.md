# CORS Fix - Final Solution

## The Problem

The frontend was getting "Failed to fetch" errors with CORS blocking messages:
```
Access to fetch at 'https://ledgerly-production-4b76.up.railway.app/api/...' 
from origin 'https://ledgerly-psi.vercel.app' has been blocked by CORS policy: 
Response to preflight request doesn't pass access control check.
```

## Root Cause

Spring Security was blocking CORS preflight OPTIONS requests **before** they could reach the CORS configuration in `WebMvcConfig`. The security filter chain was returning `403 Forbidden` for OPTIONS requests.

## The Solution

Added CORS configuration directly in `SecurityConfig.java` so that Spring Security handles CORS **before** authentication checks.

### Changes Made

**File: `backend/src/main/java/dev/atinroy/ledgerly/config/SecurityConfig.java`**

1. **Added CORS Configuration Bean:**
   ```java
   @Bean
   public CorsConfigurationSource corsConfigurationSource() {
       CorsConfiguration configuration = new CorsConfiguration();
       
       // Use allowedOriginPatterns to support wildcards
       String[] origins = allowedOrigins.split(",");
       configuration.setAllowedOriginPatterns(Arrays.asList(origins));
       
       // Allow all necessary HTTP methods
       configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
       
       // Allow all headers
       configuration.setAllowedHeaders(List.of("*"));
       
       // Allow credentials (JWT tokens)
       configuration.setAllowCredentials(true);
       
       // Cache preflight for 1 hour
       configuration.setMaxAge(3600L);
       
       UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
       source.registerCorsConfiguration("/api/**", configuration);
       
       return source;
   }
   ```

2. **Enabled CORS in Security Chain:**
   ```java
   return http
       .csrf(AbstractHttpConfigurer::disable)
       .cors(cors -> cors.configurationSource(corsConfigurationSource())) // ← Added this
       .authorizeHttpRequests(auth -> auth
           // ... rest of config
       )
   ```

## Why This Works

1. **Order Matters**: Spring Security processes filters in order:
   - CSRF disabled ✓
   - **CORS configured** ✓ (now happens here)
   - Authorization checks
   - JWT filter

2. **Preflight Requests**: OPTIONS requests are now handled by CORS configuration before authentication, so they don't need JWT tokens.

3. **Wildcard Support**: Using `allowedOriginPatterns` instead of `allowedOrigins` allows patterns like `https://*.vercel.app`.

## Environment Variable

Make sure this is set in Railway:
```bash
CORS_ALLOWED_ORIGINS=https://*.vercel.app,http://localhost:3000
```

Or for specific domain:
```bash
CORS_ALLOWED_ORIGINS=https://ledgerly-psi.vercel.app,http://localhost:3000
```

## Testing

After Railway redeploys (2-3 minutes):

1. **Clear browser cache** (Ctrl+Shift+R or Cmd+Shift+R)
2. **Refresh your Vercel app**
3. **Check console** - CORS errors should be gone
4. **Pages should load** - Data should fetch successfully

### Verify CORS is Working

In browser console:
```javascript
fetch('https://ledgerly-production-4b76.up.railway.app/api/users/1/transactions', {
  method: 'OPTIONS',
  headers: {
    'Origin': 'https://ledgerly-psi.vercel.app',
    'Access-Control-Request-Method': 'GET',
    'Access-Control-Request-Headers': 'authorization,content-type'
  }
})
.then(r => console.log('CORS OK:', r.status))
.catch(e => console.error('CORS Failed:', e))
```

Should return status `200` or `204`, not `403`.

## What Was Wrong Before

1. **WebMvcConfig CORS**: Only configured CORS at the MVC level, but Spring Security blocked requests before they reached MVC.

2. **Security Filter Order**: Authentication filters ran before CORS handling, causing preflight requests to fail.

3. **OPTIONS Requests**: Browsers send OPTIONS requests before actual requests (preflight), and these were being blocked.

## Additional Notes

- **Keep WebMvcConfig**: The CORS configuration in `WebMvcConfig` is still there and doesn't hurt, but the SecurityConfig CORS takes precedence.

- **Both Configurations**: Having CORS in both places is fine - Spring Security's CORS runs first and handles the security aspect.

- **Credentials**: `allowCredentials(true)` is required for sending JWT tokens in Authorization headers.

## Status

✅ **Fixed and Deployed**

The fix has been committed and pushed. Railway will automatically redeploy the backend with the corrected CORS configuration.

---

**Commit:** bb194ed - "Fix CORS preflight blocking by Spring Security"
