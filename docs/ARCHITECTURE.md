# OpenAPI Architecture Overview

## System Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                         Browser / Client                         │
└─────────────────────────────────────────────────────────────────┘
                                 │
                                 ↓
        ┌────────────────────────────────────────┐
        │   Swagger UI (/swagger-ui.html)       │
        │   - Interactive documentation           │
        │   - "Authorize" button for JWT         │
        │   - "Try it out" for testing           │
        └────────────────────────────────────────┘
                                 │
                                 ↓
        ┌────────────────────────────────────────┐
        │   OpenAPI Spec (/v3/api-docs)          │
        │   - Generated from annotations          │
        │   - Contains all endpoint metadata     │
        └────────────────────────────────────────┘
                                 │
                                 ↓
┌─────────────────────────────────────────────────────────────────┐
│                      Spring Boot Application                     │
│                                                                   │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │              SecurityConfig                                 │ │
│  │  - Permits: /swagger-ui/**, /v3/api-docs/**               │ │
│  │  - Requires JWT: /api/** (except /api/auth/**)            │ │
│  └────────────────────────────────────────────────────────────┘ │
│                                 │                                │
│                                 ↓                                │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │              OpenApiConfig                                  │ │
│  │  - Defines SecurityScheme (JWT Bearer)                     │ │
│  │  - Sets API metadata (title, version, etc.)               │ │
│  │  - Applies global security requirement                     │ │
│  └────────────────────────────────────────────────────────────┘ │
│                                 │                                │
│                                 ↓                                │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │                    Controllers                            │  │
│  │                                                            │  │
│  │  ┌──────────────────────────────────────────────────┐   │  │
│  │  │  AuthController                                   │   │  │
│  │  │  @Tag(name = "Authentication")                   │   │  │
│  │  │  @SecurityRequirements ← Overrides global auth   │   │  │
│  │  │                                                    │   │  │
│  │  │  /api/auth/register ← No JWT required            │   │  │
│  │  │  /api/auth/login    ← No JWT required            │   │  │
│  │  │  /api/auth/refresh  ← No JWT required            │   │  │
│  │  └──────────────────────────────────────────────────┘   │  │
│  │                                                            │  │
│  │  ┌──────────────────────────────────────────────────┐   │  │
│  │  │  TransactionController                            │   │  │
│  │  │  @Tag(name = "Transactions")                     │   │  │
│  │  │  @SecurityRequirement ← Requires JWT             │   │  │
│  │  │                                                    │   │  │
│  │  │  POST   /api/users/{id}/transactions             │   │  │
│  │  │  GET    /api/users/{id}/transactions             │   │  │
│  │  │  GET    /api/users/{id}/transactions/{txId}      │   │  │
│  │  │  PUT    /api/users/{id}/transactions/{txId}      │   │  │
│  │  │  DELETE /api/users/{id}/transactions/{txId}      │   │  │
│  │  └──────────────────────────────────────────────────┘   │  │
│  │                                                            │  │
│  │  ┌──────────────────────────────────────────────────┐   │  │
│  │  │  Other Controllers                                │   │  │
│  │  │  - BillController                                 │   │  │
│  │  │  - BudgetController                               │   │  │
│  │  │  - CategoryController                             │   │  │
│  │  │  - PartyController                                │   │  │
│  │  │  - PotController                                  │   │  │
│  │  │  - UserController                                 │   │  │
│  │  └──────────────────────────────────────────────────┘   │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                 │                                │
│                                 ↓                                │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │                         DTOs                                │ │
│  │                                                              │ │
│  │  Request DTOs:                Response DTOs:                │ │
│  │  - LoginRequest          →    - AuthResponse               │ │
│  │  - RegisterRequest       →    - TransactionResponse        │ │
│  │  - TransactionCreate...  →    - CategoryResponse           │ │
│  │                                                              │ │
│  │  Each field can have:                                       │ │
│  │  - @Schema(description, example)                           │ │
│  │  - Validation constraints (@NotBlank, @Email, etc.)        │ │
│  └────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

---

## Request Flow with JWT

### 1. Public Endpoint (No Authentication)

```
User Browser
    │
    ↓ POST /api/auth/login
    │ Body: {"email": "...", "password": "..."}
    │
    ↓
SecurityConfig
    │ ✓ Matches /api/auth/** → permitAll()
    │
    ↓
AuthController
    │ @SecurityRequirements (empty) ← No JWT needed
    │ @Operation documents the endpoint
    │
    ↓
AuthService.login()
    │ Validates credentials
    │ Generates JWT tokens
    │
    ↓
AuthResponse
    │ {"accessToken": "...", "refreshToken": "...", ...}
    │
    ↓
User receives tokens
```

### 2. Protected Endpoint (JWT Required)

```
User Browser
    │
    ↓ GET /api/users/1/transactions
    │ Header: Authorization: Bearer eyJhbGci...
    │
    ↓
SecurityConfig
    │ ✓ Matches /api/** → authenticated()
    │
    ↓
JwtAuthenticationFilter
    │ ✓ Validates JWT token
    │ ✓ Extracts user info
    │ ✓ Sets SecurityContext
    │
    ↓
TransactionController
    │ @SecurityRequirement ← JWT enforced
    │ @Operation documents the endpoint
    │
    ↓
TransactionService.getTransactions()
    │ Business logic
    │
    ↓
List<TransactionResponse>
    │ [{"id": 1, "amount": 50.0, ...}, ...]
    │
    ↓
User receives data
```

---

## OpenAPI Documentation Generation

```
                ┌─────────────────────────┐
                │  Spring Boot Startup    │
                └───────────┬─────────────┘
                            │
                            ↓
        ┌───────────────────────────────────────┐
        │  springdoc-openapi scans for:         │
        │  - @RestController classes             │
        │  - @RequestMapping mappings            │
        │  - Method parameters & return types    │
        │  - @Operation, @Tag, @Schema, etc.    │
        └───────────────────┬───────────────────┘
                            │
                            ↓
        ┌───────────────────────────────────────┐
        │  Reads OpenApiConfig bean             │
        │  - SecuritySchemes                     │
        │  - Global SecurityRequirement          │
        │  - API Info (title, version, etc.)    │
        └───────────────────┬───────────────────┘
                            │
                            ↓
        ┌───────────────────────────────────────┐
        │  Generates OpenAPI 3.0 Specification  │
        │  - Endpoints grouped by @Tag           │
        │  - Request/response schemas from DTOs │
        │  - Security requirements per endpoint │
        │  - Validation constraints documented  │
        └───────────────────┬───────────────────┘
                            │
                            ↓
        ┌───────────────────────────────────────┐
        │  Exposes at /v3/api-docs (JSON/YAML)  │
        │  Serves Swagger UI at /swagger-ui.html│
        └───────────────────────────────────────┘
```

---

## Authentication Flow in Swagger UI

```
┌─────────────────────────────────────────────────────────────┐
│ Step 1: User opens Swagger UI                               │
│ http://localhost:8080/swagger-ui.html                       │
└─────────────────┬───────────────────────────────────────────┘
                  │
                  ↓
┌─────────────────────────────────────────────────────────────┐
│ Step 2: User tests /api/auth/login                          │
│ - Click "Try it out"                                         │
│ - Enter: {"email": "user@example.com", "password": "..."}  │
│ - Click "Execute"                                            │
└─────────────────┬───────────────────────────────────────────┘
                  │
                  ↓
┌─────────────────────────────────────────────────────────────┐
│ Step 3: User receives AuthResponse                          │
│ {                                                            │
│   "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", │
│   "refreshToken": "...",                                     │
│   "userId": 1,                                               │
│   ...                                                        │
│ }                                                            │
└─────────────────┬───────────────────────────────────────────┘
                  │
                  ↓
┌─────────────────────────────────────────────────────────────┐
│ Step 4: User clicks "Authorize" button (🔓)                 │
│ - Modal opens: "Bearer Authentication"                      │
│ - User pastes accessToken                                   │
│ - Clicks "Authorize" then "Close"                           │
└─────────────────┬───────────────────────────────────────────┘
                  │
                  ↓
┌─────────────────────────────────────────────────────────────┐
│ Step 5: Token stored in Swagger UI                          │
│ - All subsequent requests include:                          │
│   Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9│
│ - Lock icon (🔒) appears (indicating authorized)            │
└─────────────────┬───────────────────────────────────────────┘
                  │
                  ↓
┌─────────────────────────────────────────────────────────────┐
│ Step 6: User can now test protected endpoints               │
│ - GET /api/users/1/transactions                             │
│ - POST /api/users/1/categories                              │
│ - etc.                                                       │
└─────────────────────────────────────────────────────────────┘
```

---

## Component Interactions

```
┌──────────────────────────────────────────────────────────────────┐
│                        Component Diagram                          │
└──────────────────────────────────────────────────────────────────┘

┌────────────────┐
│  Swagger UI    │  Reads OpenAPI spec, renders interactive docs
└───────┬────────┘
        │
        │ GET /v3/api-docs
        │
        ↓
┌────────────────────────────────────────────────────────────────┐
│  springdoc-openapi library                                      │
│  - Scans @RestController classes at startup                    │
│  - Reads OpenApiConfig bean                                    │
│  - Generates OpenAPI 3.0 JSON/YAML                            │
│  - Serves Swagger UI static files                             │
└───────┬────────────────────────────────────────────────────────┘
        │
        │ Reads annotations from
        │
        ↓
┌────────────────────────────────────────────────────────────────┐
│  Controllers                                                    │
│  - @Tag: Groups endpoints                                      │
│  - @Operation: Describes operation                             │
│  - @ApiResponses: Documents status codes                       │
│  - @SecurityRequirement / @SecurityRequirements                │
└───────┬────────────────────────────────────────────────────────┘
        │
        │ Uses
        │
        ↓
┌────────────────────────────────────────────────────────────────┐
│  DTOs (Request & Response)                                     │
│  - @Schema: Describes class/field                              │
│  - Validation annotations: @NotBlank, @Email, @Size, etc.     │
└───────┬────────────────────────────────────────────────────────┘
        │
        │ Processed by
        │
        ↓
┌────────────────────────────────────────────────────────────────┐
│  Services (AuthService, TransactionService, etc.)              │
│  - Business logic (unchanged)                                  │
│  - No OpenAPI code here                                        │
└────────────────────────────────────────────────────────────────┘
```

---

## Security Integration

```
┌──────────────────────────────────────────────────────────────────┐
│                    Security Architecture                          │
└──────────────────────────────────────────────────────────────────┘

    HTTP Request
         │
         ↓
    ┌─────────────────────────────────────┐
    │  Spring Security Filter Chain       │
    └─────────────────┬───────────────────┘
                      │
                      ↓
    ┌─────────────────────────────────────┐
    │  SecurityConfig                     │
    │  - Public: /api/auth/**             │
    │  - Public: /swagger-ui/**           │ ← NEW
    │  - Public: /v3/api-docs/**          │ ← NEW
    │  - Protected: /api/**               │
    └─────────────────┬───────────────────┘
                      │
                      ↓
         ┌────────────┴─────────────┐
         │                          │
    Public Path                Protected Path
         │                          │
         ↓                          ↓
    ┌─────────────┐     ┌──────────────────────┐
    │  Allowed    │     │ JwtAuthenticationFilter │
    └─────────────┘     └──────────┬─────────────┘
                                   │
                                   ↓ Validates JWT
                        ┌──────────────────────┐
                        │  JwtService          │
                        └──────────┬───────────┘
                                   │
                                   ↓ Valid?
                        ┌──────────────────────┐
                        │  Controller          │
                        └──────────────────────┘

OpenAPI Configuration:
- SecurityScheme defines "Bearer Authentication" format
- @SecurityRequirement applies JWT to endpoints
- @SecurityRequirements (empty) excludes JWT from endpoints
```

---

## File Dependencies

```
OpenApiConfig.java
    ↓ Configures
io.swagger.v3.oas.models.*
    ↓ Used by
Controllers (@Tag, @Operation, @ApiResponses)
    ↓ References
DTOs (@Schema)
    ↓ Scanned by
springdoc-openapi-starter-webmvc-ui
    ↓ Generates
OpenAPI 3.0 Specification (JSON/YAML)
    ↓ Consumed by
Swagger UI (HTML/CSS/JS)
    ↓ Accessed by
Browser (http://localhost:8080/swagger-ui.html)
```

---

## Summary

**Three key pieces make this work:**

1. **OpenApiConfig** - Defines security scheme and API metadata
2. **Controller Annotations** - Document endpoints and security requirements
3. **DTO Annotations** - Document request/response structures

**Result:** Interactive API documentation with JWT authentication support!
