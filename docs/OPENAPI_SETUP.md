# OpenAPI/Swagger Documentation Setup

## Overview

This document explains the OpenAPI (Swagger) documentation setup for the Ledgerly REST API.

---

## 1. Dependency

The following dependency has been added to `backend/build.gradle.kts`:

```kotlin
implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")
```

**Why this dependency?**
- `springdoc-openapi-starter-webmvc-ui` is the modern, actively maintained OpenAPI library for Spring Boot
- Version 2.7.0 is compatible with Spring Boot 4.0.1 and Spring MVC
- Includes both OpenAPI spec generation and Swagger UI

---

## 2. Configuration Class

**Location:** `backend/src/main/java/dev/atinroy/ledgerly/config/OpenApiConfig.java`

This single configuration class:
- Defines API metadata (title, version, description)
- Configures JWT Bearer authentication scheme
- Makes the "Authorize" button appear in Swagger UI
- Applies JWT authentication globally to all endpoints (except those explicitly excluded)

### Key Components:

```java
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(...)                    // API metadata
            .addServersItem(...)          // Server URL
            .components(...)              // Security schemes
            .addSecurityItem(...);        // Global security requirement
    }
}
```

**Security Scheme:**
- Type: `HTTP`
- Scheme: `bearer`
- Bearer Format: `JWT`
- This allows Swagger UI to add `Authorization: Bearer <token>` header automatically

---

## 3. Security Configuration Update

**Location:** `backend/src/main/java/dev/atinroy/ledgerly/config/SecurityConfig.java`

Added the following matchers to permit Swagger UI access:

```java
.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
```

**Why these paths?**
- `/swagger-ui/**` - Swagger UI static resources
- `/v3/api-docs/**` - OpenAPI JSON/YAML specification
- `/swagger-ui.html` - Swagger UI entry point

---

## 4. Controller Annotations

### 4.1 Authentication Controller (Public Endpoints)

**Location:** `backend/src/main/java/dev/atinroy/ledgerly/controller/AuthController.java`

**Class-level annotations:**
```java
@Tag(name = "Authentication", description = "User authentication and registration endpoints")
```

**Method-level annotations example:**
```java
@Operation(summary = "Authenticate user", description = "Authenticates user credentials and returns JWT tokens")
@SecurityRequirements  // This overrides global security - no auth needed
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Login successful",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
})
```

**Key points:**
- `@Tag` groups endpoints in Swagger UI
- `@SecurityRequirements` (empty) overrides global JWT requirement for public endpoints
- `@ApiResponses` documents possible HTTP status codes
- `@Content` and `@Schema` specify response body type

### 4.2 Protected Controller (Transaction Example)

**Location:** `backend/src/main/java/dev/atinroy/ledgerly/controller/TransactionController.java`

**Class-level annotations:**
```java
@Tag(name = "Transactions", description = "Transaction management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
```

**Method-level annotations example:**
```java
@Operation(summary = "Create a new transaction", description = "Creates a new transaction for the specified user")
@ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Transaction created successfully",
            content = @Content(schema = @Schema(implementation = TransactionResponse.class))),
    @ApiResponse(responseCode = "400", description = "Invalid transaction data"),
    @ApiResponse(responseCode = "401", description = "Unauthorized"),
    @ApiResponse(responseCode = "404", description = "User not found")
})
```

**Key points:**
- `@SecurityRequirement` at class level applies JWT auth to all methods
- Standard status codes: 200 (OK), 201 (Created), 400 (Bad Request), 401 (Unauthorized), 404 (Not Found)

---

## 5. DTO Documentation

### Request DTO Example

**Location:** `backend/src/main/java/dev/atinroy/ledgerly/dto/request/auth/LoginRequest.java`

```java
@Schema(description = "Login request payload")
public record LoginRequest(
    @Schema(description = "User email address", example = "user@example.com")
    @NotBlank String email,
    
    @Schema(description = "User password", example = "Password123!")
    @NotBlank String password
) {}
```

### Response DTO Example

**Location:** `backend/src/main/java/dev/atinroy/ledgerly/dto/response/AuthResponse.java`

```java
@Schema(description = "Authentication response containing tokens and user information")
public record AuthResponse(
    @Schema(description = "JWT access token for API authorization", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    String accessToken,
    
    @Schema(description = "JWT refresh token for obtaining new access tokens")
    String refreshToken,
    
    // ... other fields
) {}
```

**Key points:**
- `@Schema` at class level describes the overall DTO
- `@Schema` at field level provides field-specific documentation
- `example` attribute shows sample values in Swagger UI
- Validation annotations (`@NotBlank`, etc.) are automatically reflected in the spec

---

## 6. Accessing Swagger UI

### Development URLs:

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs
- **OpenAPI YAML:** http://localhost:8080/v3/api-docs.yaml

### Using JWT Authentication in Swagger UI:

1. Navigate to http://localhost:8080/swagger-ui.html
2. Click the **"Authorize"** button (🔓 icon, top-right)
3. In the "Bearer Authentication" section, enter your JWT token (without "Bearer " prefix)
4. Click **"Authorize"** then **"Close"**
5. All subsequent API calls will include the `Authorization: Bearer <token>` header

### Getting a JWT Token:

1. Use the `POST /api/auth/login` endpoint in Swagger UI
2. Provide valid credentials in the request body
3. Copy the `accessToken` from the response
4. Use it in the "Authorize" dialog

---

## 7. Applying to Other Controllers

To document other controllers (BillController, CategoryController, etc.), follow this pattern:

### Minimal Approach (Recommended):

```java
@RestController
@RequestMapping("/api/users/{userId}/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Category management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class CategoryController {
    // Your existing methods - no changes needed
}
```

### Enhanced Approach (Optional):

Add `@Operation` and `@ApiResponses` to specific methods if you want more detailed documentation:

```java
@GetMapping("/{categoryId}")
@Operation(summary = "Get category by ID", description = "Retrieves a specific category")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Category found"),
    @ApiResponse(responseCode = "401", description = "Unauthorized"),
    @ApiResponse(responseCode = "404", description = "Category not found")
})
public ResponseEntity<CategoryResponse> getCategory(@PathVariable Long userId, @PathVariable Long categoryId) {
    // existing implementation
}
```

---

## 8. Best Practices

### DO:
✅ Use `@Tag` to group related endpoints
✅ Use `@SecurityRequirements` for public endpoints (auth endpoints)
✅ Use `@SecurityRequirement` for protected endpoints
✅ Document DTOs with `@Schema` annotations
✅ Use meaningful examples in `@Schema(example = "...")`
✅ Document validation constraints on DTOs (already done via Jakarta Validation)
✅ Use standard HTTP status codes

### DON'T:
❌ Add annotations everywhere - use them strategically
❌ Hardcode JWT tokens, secrets, or passwords
❌ Expose refresh tokens in examples (use placeholder)
❌ Add multiple OpenAPI configuration classes
❌ Disable Spring Security for Swagger access
❌ Change existing controller logic

---

## 9. Customization

### Change Server URL:

In `OpenApiConfig.java`:
```java
.addServersItem(new Server()
    .url("https://api.production.com")
    .description("Production server"))
```

### Change Swagger UI Path:

In `application.yml` or `application.properties`:
```yaml
springdoc:
  swagger-ui:
    path: /api-docs  # Changes URL to /api-docs instead of /swagger-ui.html
```

### Exclude Endpoints from Documentation:

```java
@Hidden  // Add to any controller or method
public class InternalController { ... }
```

---

## 10. Troubleshooting

### Swagger UI not loading?
- Verify the dependency is in `build.gradle.kts`
- Check that Swagger paths are permitted in `SecurityConfig`
- Ensure the application is running on the expected port

### "Authorize" button not showing?
- Verify `OpenApiConfig.java` includes `.components()` with SecurityScheme
- Check that `.addSecurityItem()` is called

### 401/403 errors when testing endpoints?
- Click "Authorize" and enter a valid JWT token
- Verify the token hasn't expired
- Check that the token includes the "Bearer " prefix in actual HTTP calls (Swagger adds it automatically)

### DTOs not showing examples?
- Add `@Schema(example = "...")` to DTO fields
- Ensure DTOs are Java records or have proper getters/setters

---

## Summary

This setup provides:
- ✅ Complete OpenAPI 3.0 documentation
- ✅ Interactive Swagger UI at `/swagger-ui.html`
- ✅ JWT Bearer authentication support
- ✅ Minimal, non-invasive annotations
- ✅ Auto-generated request/response schemas
- ✅ Validation constraint documentation
- ✅ No changes to existing business logic

The documentation is now live and accessible via Swagger UI!
