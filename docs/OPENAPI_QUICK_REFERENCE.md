# OpenAPI Quick Reference

## Access Points

| Resource | URL |
|----------|-----|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |
| OpenAPI YAML | http://localhost:8080/v3/api-docs.yaml |

---

## Quick Start

1. **Start your application**
2. **Navigate to** http://localhost:8080/swagger-ui.html
3. **Get a token:**
   - Use `POST /api/auth/login`
   - Copy the `accessToken` from response
4. **Authorize:**
   - Click "Authorize" button (🔓)
   - Paste your token
   - Click "Authorize" then "Close"
5. **Test endpoints** - all calls now include your JWT token

---

## File Locations

```
backend/
├── build.gradle.kts                                  # Dependency added here
└── src/main/java/dev/atinroy/ledgerly/
    ├── config/
    │   ├── OpenApiConfig.java                        # NEW: OpenAPI configuration
    │   └── SecurityConfig.java                       # UPDATED: Added Swagger paths
    ├── controller/
    │   ├── AuthController.java                       # UPDATED: Added annotations
    │   └── TransactionController.java                # UPDATED: Example annotations
    └── dto/
        ├── request/auth/LoginRequest.java            # UPDATED: Added @Schema
        └── response/AuthResponse.java                # UPDATED: Added @Schema
```

---

## Annotation Cheat Sheet

### Controller Class Level

```java
// For protected endpoints
@Tag(name = "Resource Name", description = "Description")
@SecurityRequirement(name = "Bearer Authentication")

// For public endpoints (auth)
@Tag(name = "Authentication", description = "Description")
// No @SecurityRequirement needed here
```

### Method Level

```java
// Public endpoint (override global security)
@SecurityRequirements  // Empty = no auth required

// Document operation
@Operation(summary = "Short description", description = "Longer description")

// Document responses
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Success",
            content = @Content(schema = @Schema(implementation = YourDTO.class))),
    @ApiResponse(responseCode = "401", description = "Unauthorized")
})
```

### DTO Fields

```java
@Schema(description = "Field description", example = "sample-value")
@NotBlank  // Validation annotations are automatically documented
String fieldName;
```

---

## Common HTTP Status Codes

| Code | Meaning | When to Use |
|------|---------|-------------|
| 200 | OK | Successful GET, PUT, DELETE |
| 201 | Created | Successful POST (resource created) |
| 204 | No Content | Successful DELETE with no response body |
| 400 | Bad Request | Validation error, invalid input |
| 401 | Unauthorized | Missing or invalid JWT token |
| 403 | Forbidden | Valid token but insufficient permissions |
| 404 | Not Found | Resource doesn't exist |
| 500 | Internal Server Error | Unexpected server error |

---

## Pattern for Each Controller

### Minimal Setup (Recommended)

```java
@RestController
@RequestMapping("/api/resource")
@RequiredArgsConstructor
@Tag(name = "Resource", description = "Resource management")
@SecurityRequirement(name = "Bearer Authentication")  // If protected
public class ResourceController {
    // Existing methods - no changes needed
    // OpenAPI will auto-generate docs from method signatures
}
```

### With Method Documentation (Optional)

```java
@GetMapping("/{id}")
@Operation(summary = "Get by ID")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Found"),
    @ApiResponse(responseCode = "404", description = "Not found")
})
public ResponseEntity<ResourceResponse> getById(@PathVariable Long id) {
    // existing code
}
```

---

## Testing Workflow

1. **Login** via `/api/auth/login` in Swagger UI
2. **Copy token** from response
3. **Click "Authorize"** button
4. **Paste token** (without "Bearer " prefix)
5. **Test protected endpoints** - token is automatically included

---

## Configuration Options (Optional)

Add to `application.yml` if you want to customize:

```yaml
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    operationsSorter: method
    tagsSorter: alpha
  show-actuator: false
```

---

## Common Issues & Fixes

| Problem | Solution |
|---------|----------|
| Swagger UI shows 404 | Check SecurityConfig permits `/swagger-ui/**` |
| No "Authorize" button | Verify OpenApiConfig has SecurityScheme setup |
| 401 errors on all requests | Click "Authorize" and add your JWT token |
| Token expired errors | Get a new token via `/api/auth/login` |
| DTO fields not documented | Add `@Schema` annotations to DTO fields |

---

## Next Steps

To document remaining controllers, add these annotations to:
- [ ] BillController
- [ ] BudgetController
- [ ] CategoryController
- [ ] PartyController
- [ ] PotController
- [ ] UserController

Just add `@Tag` and `@SecurityRequirement` at class level. Method-level annotations are optional!

---

## Resources

- **springdoc-openapi docs:** https://springdoc.org/
- **OpenAPI Specification:** https://spec.openapis.org/oas/latest.html
- **Swagger UI docs:** https://swagger.io/tools/swagger-ui/
