# Documentation Index

This folder contains documentation for the Ledgerly REST API.

---

## OpenAPI/Swagger Documentation

### Quick Start
👉 **Start here:** [OPENAPI_SUMMARY.md](OPENAPI_SUMMARY.md)

### Detailed Guides

| Document | Purpose | When to Read |
|----------|---------|--------------|
| [OPENAPI_SUMMARY.md](OPENAPI_SUMMARY.md) | Executive summary of what was implemented | **Read this first** - Overview and next steps |
| [OPENAPI_QUICK_REFERENCE.md](OPENAPI_QUICK_REFERENCE.md) | Cheat sheet for daily use | Quick lookup for annotations, URLs, patterns |
| [OPENAPI_SETUP.md](OPENAPI_SETUP.md) | Comprehensive setup guide | Deep dive into configuration, customization, troubleshooting |
| [OPENAPI_CHANGES.md](OPENAPI_CHANGES.md) | Detailed changelog | See exactly what was changed in each file |

---

## Quick Access

### Swagger UI
- **URL:** http://localhost:8080/swagger-ui.html
- **Purpose:** Interactive API documentation and testing

### OpenAPI Specification
- **JSON:** http://localhost:8080/v3/api-docs
- **YAML:** http://localhost:8080/v3/api-docs.yaml
- **Purpose:** Machine-readable API specification

---

## Getting Started

1. **Sync Gradle dependencies**
   ```bash
   ./gradlew build --refresh-dependencies
   ```

2. **Start your application**
   ```bash
   ./gradlew bootRun
   ```

3. **Open Swagger UI**
   - Navigate to: http://localhost:8080/swagger-ui.html

4. **Authenticate**
   - Use `/api/auth/login` to get a JWT token
   - Click "Authorize" button
   - Paste your token
   - Test protected endpoints

---

## What's Implemented

✅ Complete OpenAPI 3.0 specification  
✅ Interactive Swagger UI with JWT authentication  
✅ Documentation for Auth and Transaction endpoints (examples)  
✅ DTO documentation with examples  
✅ No changes to business logic  
✅ No security weaknesses introduced  

---

## Need Help?

- **Can't access Swagger UI?** → See [OPENAPI_SETUP.md](OPENAPI_SETUP.md) - Section 10 (Troubleshooting)
- **How do I document other controllers?** → See [OPENAPI_QUICK_REFERENCE.md](OPENAPI_QUICK_REFERENCE.md) - Section "Pattern for Each Controller"
- **What annotations should I use?** → See [OPENAPI_QUICK_REFERENCE.md](OPENAPI_QUICK_REFERENCE.md) - Section "Annotation Cheat Sheet"
- **What exactly changed?** → See [OPENAPI_CHANGES.md](OPENAPI_CHANGES.md)

---

## Project Structure

```
ledgerly/
├── backend/
│   ├── build.gradle.kts                    # Contains springdoc dependency
│   └── src/main/java/dev/atinroy/ledgerly/
│       ├── config/
│       │   ├── OpenApiConfig.java          # NEW: OpenAPI configuration
│       │   └── SecurityConfig.java         # UPDATED: Permits Swagger paths
│       ├── controller/
│       │   ├── AuthController.java         # UPDATED: Example annotations
│       │   └── TransactionController.java  # UPDATED: Example annotations
│       └── dto/
│           ├── request/auth/
│           │   └── LoginRequest.java       # UPDATED: @Schema examples
│           └── response/
│               └── AuthResponse.java       # UPDATED: @Schema examples
└── docs/
    ├── README.md                           # This file
    ├── OPENAPI_SUMMARY.md                  # Executive summary
    ├── OPENAPI_QUICK_REFERENCE.md          # Quick reference guide
    ├── OPENAPI_SETUP.md                    # Comprehensive guide
    └── OPENAPI_CHANGES.md                  # Detailed changelog
```

---

## Key Features

### JWT Authentication
Swagger UI supports testing authenticated endpoints:
1. Get token via `/api/auth/login`
2. Click "Authorize" button (🔓)
3. Enter token (without "Bearer " prefix)
4. All requests automatically include token

### Auto-Generated Documentation
- Request/response schemas from Java records
- Validation constraints from Jakarta Validation
- HTTP status codes from @ApiResponses
- Example values from @Schema annotations

### Non-Invasive Implementation
- Existing code unchanged
- Only documentation annotations added
- No impact on performance
- No security compromises

---

## Next Steps

### Optional: Document Remaining Controllers
Apply the same pattern to:
- [ ] BillController
- [ ] BudgetController
- [ ] CategoryController
- [ ] PartyController
- [ ] PotController
- [ ] UserController

See [OPENAPI_QUICK_REFERENCE.md](OPENAPI_QUICK_REFERENCE.md) for the pattern.

---

## Resources

- **springdoc-openapi:** https://springdoc.org/
- **OpenAPI Specification:** https://spec.openapis.org/oas/latest.html
- **Swagger UI:** https://swagger.io/tools/swagger-ui/
- **Jakarta Validation:** https://jakarta.ee/specifications/bean-validation/

---

**Ready to explore your API?** 🚀

Open http://localhost:8080/swagger-ui.html and start testing!
