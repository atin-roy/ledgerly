# Ledgerly - Personal Finance Management API

A full-stack personal finance management application built with Spring Boot (backend) and modern frontend technologies.

## Overview

Ledgerly helps users manage their personal finances with features for tracking transactions, budgeting, bills, pots (savings goals), and recurring expenses.

## Tech Stack

### Backend
- **Framework:** Spring Boot 4.0.1
- **Language:** Java 25
- **Authentication:** JWT (JJWT 0.12.6)
- **Database:** PostgreSQL 18+
- **ORM:** JPA/Hibernate
- **Validation:** Spring Validation
- **API Documentation:** SpringDoc OpenAPI (Swagger)
- **Build Tool:** Gradle

### Frontend
- **Framework:** HTML5/JavaScript
- **Assets:** Static HTML pages with modern UI
- **Icons & Fonts:** Custom design system

## Project Structure

```
ledgerly/
├── backend/                    # Spring Boot API
│   ├── src/main/java/         # Application code
│   ├── src/main/resources/    # Configuration
│   ├── build.gradle.kts       # Gradle configuration
│   └── compose.yaml           # Local PostgreSQL setup
├── packages/                   # Frontend assets
│   ├── starter-code/          # Static pages
│   │   ├── assets/            # Images, fonts
│   │   └── *.html             # Main pages
│   └── bruno/                 # API testing (Bruno)
├── docs/                       # Documentation
│   ├── ARCHITECTURE.md        # System design
│   └── LedgerlyAPI/           # Bruno collections
└── README.md                   # This file
```

## Getting Started

### Prerequisites

- Java 25 (or Java 21 LTS recommended for production)
- PostgreSQL 18+
- Gradle (included via wrapper)

### Local Development

#### 1. Start PostgreSQL

```bash
cd backend
docker-compose -f compose.yaml up -d
# or use: docker-compose up -d
```

#### 2. Build Backend

```bash
cd backend
./gradlew clean build
```

#### 3. Run Backend

```bash
./gradlew bootRun
```

The API will be available at `http://localhost:8080`

#### 4. Access Swagger UI

Open your browser to: `http://localhost:8080/swagger-ui.html`

## API Endpoints

### Authentication (Public)
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get JWT tokens
- `POST /api/auth/refresh` - Refresh access token

### Protected Resources
All endpoints under `/api/**` (except `/api/auth/**`) require JWT authentication.

**Available Resources:**
- `/api/users` - User management
- `/api/transactions` - Transaction tracking
- `/api/categories` - Expense categories
- `/api/budgets` - Budget planning
- `/api/bills` - Bill tracking
- `/api/pots` - Savings goals
- `/api/parties` - People/entities you transact with

**Authentication Header:**
```
Authorization: Bearer <your_jwt_token>
```

## Configuration

### Development (Default)
```bash
./gradlew bootRun
```
Uses default dev settings (localhost database, dev credentials)

### Production
Set environment variables:
```bash
export SPRING_PROFILES_ACTIVE=prod
export DATABASE_URL=jdbc:postgresql://host:5432/database
export DATABASE_USERNAME=db_user
export DATABASE_PASSWORD=secure_password
export JWT_SECRET=<your-256-bit-secret>
```

Then run:
```bash
./gradlew bootJar
java -jar build/libs/finance-backend-0.0.1-SNAPSHOT.jar
```

## Configuration Files

- `backend/src/main/resources/application.properties` - Development defaults
- `backend/src/main/resources/application-prod.properties` - Production settings

## Database

### Schema Management

The application uses JPA entity definitions for schema management:

- **Development:** Auto-creates/updates schema (`ddl-auto=update`)
- **Production:** Validates schema without modifications (`ddl-auto=validate`)

### Supported Entities
- Users (with roles: USER, ADMIN)
- Transactions
- Categories
- Budgets
- Bills
- Pots (savings goals)
- Parties (transaction counterparties)

## Security

- **Password Encryption:** BCrypt
- **Session Management:** Stateless JWT
- **CORS:** Configurable per environment
- **Production Hardening:**
  - Swagger/OpenAPI disabled in production
  - Error messages sanitized
  - Actuator endpoints restricted
  - SQL logging disabled
  - Secrets externalized

## Development

### Running Tests

```bash
cd backend
./gradlew test
```

### Code Structure

```
backend/src/main/java/dev/atinroy/ledgerly/
├── config/          # Spring configurations (Security, OpenAPI, WebMvc)
├── controller/      # REST endpoints
├── service/         # Business logic
├── repository/      # Data access (JPA)
├── entity/          # JPA entities
├── dto/             # Request/Response models
├── security/        # JWT and authentication
├── error/           # Exception handling
├── mapper/          # Entity ↔ DTO mapping (MapStruct)
└── validator/       # Input validation
```

### Key Files

- `FinanceBackendApplication.java` - Application entry point
- `SecurityConfig.java` - Spring Security configuration
- `GlobalExceptionHandler.java` - Centralized error handling
- `JwtService.java` - JWT token generation/validation

## API Documentation

- **Interactive Docs:** Available at `/swagger-ui.html` (dev only)
- **OpenAPI Spec:** Available at `/v3/api-docs` (dev only)
- **Bruno Collections:** See `packages/bruno/` for request examples

## Testing the API

### Using Bruno (Recommended)
Bruno is a fast, lightweight REST client bundled in `packages/bruno/`

### Using cURL

**Register:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "username": "username",
    "password": "SecurePass123!"
  }'
```

**Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "SecurePass123!"
  }'
```

**Access Protected Endpoint:**
```bash
curl -H "Authorization: Bearer <token>" \
  http://localhost:8080/api/users
```

## Deployment

### Requirements
- Java runtime (Java 25 or 21 LTS)
- PostgreSQL database
- Environment variables configured (see Configuration section)

### Quick Deploy

1. Build: `./gradlew bootJar`
2. Set environment variables
3. Run: `java -jar build/libs/finance-backend-0.0.1-SNAPSHOT.jar`

### Production Checklist
- [ ] `SPRING_PROFILES_ACTIVE=prod`
- [ ] All environment variables set
- [ ] Database connection verified
- [ ] JWT_SECRET is secure and unique
- [ ] HTTPS/SSL configured
- [ ] Health check responds: `/actuator/health`

## Troubleshooting

### Database Connection Error
- Verify PostgreSQL is running
- Check `DATABASE_URL` and credentials
- Ensure database user has permissions

### JWT Token Issues
- Verify token hasn't expired (30 min access token, 7 days refresh)
- Ensure `Authorization: Bearer <token>` header format
- Check `JWT_SECRET` is set in production

### Port Already in Use
```bash
# Change port (or kill process using 8080)
export SERVER_PORT=8081
./gradlew bootRun
```

## Architecture

See `docs/ARCHITECTURE.md` for detailed system design and data flow.

## Contributing

When working on this project:
1. Follow existing code conventions
2. Keep business logic in services
3. Maintain REST API standards
4. Update documentation for API changes
5. Test thoroughly before committing

## Dependencies

Key dependencies (see `build.gradle.kts` for complete list):

- Spring Boot Starter Web
- Spring Boot Starter Security
- Spring Boot Starter Data JPA
- Spring Boot Actuator
- PostgreSQL JDBC Driver
- JJWT (JWT tokens)
- Lombok (code generation)
- MapStruct (DTO mapping)
- SpringDoc OpenAPI (Swagger)

## Performance

- Connection pooling: 5-10 connections (production)
- Request size limit: 10MB
- Graceful shutdown timeout: 30 seconds
- Access token expiration: 30 minutes
- Refresh token expiration: 7 days

## Security Considerations

- Always use HTTPS in production
- Keep `JWT_SECRET` secure and unique
- Use strong database passwords
- Enable firewall rules
- Monitor logs for suspicious activity
- Keep dependencies updated
- Implement rate limiting at proxy level

## License

[Your License Here]

## Support

For issues or questions:
1. Check logs: `./gradlew bootRun` output or application logs
2. Review API documentation at `/swagger-ui.html` (dev)
3. Test endpoints using Bruno collections in `packages/bruno/`

---

**Last Updated:** January 2026  
**Current Version:** 0.0.1-SNAPSHOT
