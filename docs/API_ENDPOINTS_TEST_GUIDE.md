# Ledgerly API Endpoints - Complete Test Guide

This document provides a comprehensive guide to all API endpoints and how to test them.

## Base URL
```
http://localhost:8080
```

## Authentication
All endpoints except authentication endpoints require a Bearer token in the Authorization header:
```
Authorization: Bearer <access_token>
```

---

## 1. Authentication Endpoints (`/api/auth`)

### 1.1 Register New User
- **Endpoint:** `POST /api/auth/register`
- **Authentication:** Not required
- **Request Body:**
```json
{
  "email": "user@example.com",
  "username": "username",
  "password": "SecurePass123!"
}
```
- **Response (201):**
```json
{
  "userId": 1,
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "expiresIn": 1800
}
```
- **cURL:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email":"user@example.com",
    "username":"username",
    "password":"SecurePass123!"
  }'
```

### 1.2 Login
- **Endpoint:** `POST /api/auth/login`
- **Authentication:** Not required
- **Request Body:**
```json
{
  "email": "user@example.com",
  "password": "SecurePass123!"
}
```
- **Response (200):** Same as register
- **cURL:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email":"user@example.com",
    "password":"SecurePass123!"
  }'
```

### 1.3 Refresh Access Token
- **Endpoint:** `POST /api/auth/refresh`
- **Authentication:** Not required
- **Request Body:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```
- **Response (200):** Returns new access token
- **cURL:**
```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"<refresh_token>"}'
```

---

## 2. User Endpoints (`/api/users`)

### 2.1 Get Current User Profile
- **Endpoint:** `GET /api/users`
- **Authentication:** Required
- **Response (200):**
```json
{
  "id": 1,
  "email": "user@example.com",
  "username": "username",
  "role": "USER",
  "createdAt": "2026-01-27T08:00:00Z"
}
```
- **cURL:**
```bash
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer <token>"
```

### 2.2 Update User Profile
- **Endpoint:** `PUT /api/users`
- **Authentication:** Required
- **Request Body:**
```json
{
  "username": "newusername",
  "password": "NewPassword123!"
}
```
- **Response (200):** Updated user details
- **cURL:**
```bash
curl -X PUT http://localhost:8080/api/users \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"username":"newusername"}'
```

---

## 3. Transaction Endpoints (`/api/users/{userId}/transactions`)

### 3.1 Create Transaction
- **Endpoint:** `POST /api/users/{userId}/transactions`
- **Authentication:** Required
- **Request Body:**
```json
{
  "amount": 100.50,
  "description": "Grocery shopping",
  "type": "EXPENSE",
  "categoryId": 1,
  "partyId": 1
}
```
- **Response (201):**
```json
{
  "id": 1,
  "userId": 1,
  "amount": 100.50,
  "description": "Grocery shopping",
  "type": "EXPENSE",
  "categoryId": 1,
  "partyId": 1,
  "createdAt": "2026-01-27T08:00:00Z"
}
```
- **cURL:**
```bash
curl -X POST http://localhost:8080/api/users/1/transactions \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "amount":100.50,
    "description":"Grocery shopping",
    "type":"EXPENSE",
    "categoryId":1
  }'
```

### 3.2 Get All Transactions (Paginated)
- **Endpoint:** `GET /api/users/{userId}/transactions`
- **Authentication:** Required
- **Query Parameters:**
  - `page` (default: 0)
  - `size` (default: 20)
  - `sort` (e.g., "createdAt,desc")
- **Response (200):**
```json
{
  "content": [...],
  "pageable": {...},
  "totalElements": 10,
  "totalPages": 1,
  "last": true,
  "first": true,
  "size": 20,
  "number": 0
}
```
- **cURL:**
```bash
curl -X GET "http://localhost:8080/api/users/1/transactions?page=0&size=10" \
  -H "Authorization: Bearer <token>"
```

### 3.3 Get Single Transaction
- **Endpoint:** `GET /api/users/{userId}/transactions/{transactionId}`
- **Authentication:** Required
- **Response (200):** Single transaction object

### 3.4 Update Transaction
- **Endpoint:** `PUT /api/users/{userId}/transactions/{transactionId}`
- **Authentication:** Required
- **Request Body:** Partial update fields
- **Response (200):** Updated transaction

### 3.5 Delete Transaction
- **Endpoint:** `DELETE /api/users/{userId}/transactions/{transactionId}`
- **Authentication:** Required
- **Response (204):** No content

---

## 4. Category Endpoints (`/api/categories`)

### 4.1 Create Category
- **Endpoint:** `POST /api/categories`
- **Authentication:** Required
- **Request Body:**
```json
{
  "name": "Groceries",
  "description": "Grocery shopping and food"
}
```
- **Response (201):** Created category

### 4.2 Get All Categories
- **Endpoint:** `GET /api/categories`
- **Authentication:** Required
- **Response (200):** List of categories

### 4.3 Get Category by ID
- **Endpoint:** `GET /api/categories/{id}`
- **Authentication:** Required
- **Response (200):** Single category

### 4.4 Update Category
- **Endpoint:** `PUT /api/categories/{id}`
- **Authentication:** Required
- **Request Body:** Partial update
- **Response (200):** Updated category

### 4.5 Delete Category
- **Endpoint:** `DELETE /api/categories/{id}`
- **Authentication:** Required
- **Response (204):** No content

---

## 5. Budget Endpoints (`/api/users/{userId}/budgets`)

### 5.1 Create Budget
- **Endpoint:** `POST /api/users/{userId}/budgets`
- **Authentication:** Required
- **Request Body:**
```json
{
  "amount": 500.00,
  "period": "MONTHLY",
  "categoryId": 1
}
```
- **Response (201):** Created budget

### 5.2 Get All Budgets
- **Endpoint:** `GET /api/users/{userId}/budgets`
- **Authentication:** Required
- **Response (200):** List of budgets

### 5.3 Get Budget by ID
- **Endpoint:** `GET /api/users/{userId}/budgets/{budgetId}`
- **Authentication:** Required
- **Response (200):** Single budget

### 5.4 Update Budget
- **Endpoint:** `PUT /api/users/{userId}/budgets/{budgetId}`
- **Authentication:** Required
- **Request Body:** Partial update
- **Response (200):** Updated budget

### 5.5 Delete Budget
- **Endpoint:** `DELETE /api/users/{userId}/budgets/{budgetId}`
- **Authentication:** Required
- **Response (204):** No content

---

## 6. Bill Endpoints (`/api/users/{userId}/bills`)

### 6.1 Create Bill
- **Endpoint:** `POST /api/users/{userId}/bills`
- **Authentication:** Required
- **Request Body:**
```json
{
  "amount": 150.00,
  "description": "Monthly utilities",
  "dueDate": "2026-02-15",
  "status": "PENDING"
}
```
- **Response (201):** Created bill

### 6.2 Get All Bills
- **Endpoint:** `GET /api/users/{userId}/bills`
- **Authentication:** Required
- **Response (200):** List of bills

### 6.3 Get Bill by ID
- **Endpoint:** `GET /api/users/{userId}/bills/{billId}`
- **Authentication:** Required
- **Response (200):** Single bill

### 6.4 Update Bill
- **Endpoint:** `PUT /api/users/{userId}/bills/{billId}`
- **Authentication:** Required
- **Request Body:** Partial update (e.g., status)
- **Response (200):** Updated bill

### 6.5 Delete Bill
- **Endpoint:** `DELETE /api/users/{userId}/bills/{billId}`
- **Authentication:** Required
- **Response (204):** No content

---

## 7. Pot Endpoints (`/api/users/{userId}/pots`)
*Pots are savings goals*

### 7.1 Create Pot
- **Endpoint:** `POST /api/users/{userId}/pots`
- **Authentication:** Required
- **Request Body:**
```json
{
  "name": "Vacation Fund",
  "targetAmount": 5000.00,
  "currentAmount": 0.00
}
```
- **Response (201):** Created pot

### 7.2 Get All Pots
- **Endpoint:** `GET /api/users/{userId}/pots`
- **Authentication:** Required
- **Response (200):** List of pots

### 7.3 Get Pot by ID
- **Endpoint:** `GET /api/users/{userId}/pots/{potId}`
- **Authentication:** Required
- **Response (200):** Single pot

### 7.4 Update Pot
- **Endpoint:** `PUT /api/users/{userId}/pots/{potId}`
- **Authentication:** Required
- **Request Body:** Partial update
- **Response (200):** Updated pot

### 7.5 Delete Pot
- **Endpoint:** `DELETE /api/users/{userId}/pots/{potId}`
- **Authentication:** Required
- **Response (204):** No content

---

## 8. Party Endpoints (`/api/parties`)
*Parties are transaction counterparties (e.g., stores, people)*

### 8.1 Create Party
- **Endpoint:** `POST /api/parties`
- **Authentication:** Required
- **Request Body:**
```json
{
  "name": "Grocery Store ABC"
}
```
- **Response (201):** Created party

### 8.2 Get All Parties
- **Endpoint:** `GET /api/parties`
- **Authentication:** Required
- **Response (200):** List of parties

### 8.3 Get Party by ID
- **Endpoint:** `GET /api/parties/{id}`
- **Authentication:** Required
- **Response (200):** Single party

### 8.4 Update Party
- **Endpoint:** `PUT /api/parties/{id}`
- **Authentication:** Required
- **Request Body:** Partial update
- **Response (200):** Updated party

### 8.5 Delete Party
- **Endpoint:** `DELETE /api/parties/{id}`
- **Authentication:** Required
- **Response (204):** No content

---

## Health Check Endpoint

### Health Status
- **Endpoint:** `GET /actuator/health`
- **Authentication:** Not required
- **Response (200):**
```json
{
  "status": "UP",
  "components": {...}
}
```

---

## Error Response Format

All errors follow this format:
```json
{
  "timestamp": "2026-01-27T08:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/users"
}
```

### Common HTTP Status Codes
- `200` - OK
- `201` - Created
- `204` - No Content
- `400` - Bad Request (validation error)
- `401` - Unauthorized (missing or invalid token)
- `403` - Forbidden (insufficient permissions)
- `404` - Not Found
- `500` - Server Error

---

## Testing Workflow

### 1. Register a User
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","username":"testuser","password":"Test123456!"}' \
  | jq -r '.accessToken')
```

### 2. Create a Category
```bash
curl -X POST http://localhost:8080/api/categories \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Groceries","description":"Grocery shopping"}'
```

### 3. Create a Transaction
```bash
curl -X POST http://localhost:8080/api/users/1/transactions \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "amount":100.50,
    "description":"Grocery shopping",
    "type":"EXPENSE",
    "categoryId":1
  }'
```

### 4. Get All Transactions
```bash
curl -X GET "http://localhost:8080/api/users/1/transactions?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN"
```

---

## Running via Gradle Tests

The backend includes comprehensive integration tests that can be run with:

```bash
cd backend
./gradlew test
```

To run with code coverage:
```bash
./gradlew test jacocoTestReport
```

Coverage report: `build/reports/jacoco/test/html/index.html`

---

## Swagger/OpenAPI Documentation

Access interactive API documentation (dev mode only):
```
http://localhost:8080/swagger-ui.html
http://localhost:8080/v3/api-docs
```
