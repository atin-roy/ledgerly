# Ledgerly

A full-stack personal finance management application built with modern web technologies. Track transactions, manage budgets, monitor spending patterns, and gain insights into your financial health.

## Tech Stack

### Frontend
- **Next.js 16** - React framework with the App Router, used here as a client-rendered app (see [Architecture Decisions](#architecture-decisions))
- **TypeScript** - Type-safe development with full end-to-end type checking
- **CSS Modules** - Hand-written, scoped component styles (no utility framework or component library)

### Backend
- **Spring Boot 4.0** - Enterprise-grade Java framework with dependency injection and auto-configuration
- **Spring Security** - JWT-based authentication and authorization with bcrypt password hashing
- **Spring Data JPA** - Database abstraction layer with Hibernate ORM
- **PostgreSQL** - ACID-compliant relational database for financial data integrity
- **MapStruct** - Compile-time DTO mapping for clean separation of concerns
- **Gradle** - Build automation and dependency management

### DevOps & Tools
- **Docker Compose** - Containerized development environment
- **Bruno** - API testing and documentation
- **JaCoCo** - Code coverage reporting
- **Spring REST Docs** - API documentation generated from tests

## Features

### Financial Management
- **Transaction Tracking** - Record income and expenses with categories and detailed metadata
- **Budget Management** - Set spending limits and track progress against targets
- **Pots (Savings Goals)** - Create and monitor savings goals with visual progress indicators
- **Bills** - Track one-off and regular bills with a due date and payment status; marking a bill paid records a matching ledger transaction
- **Category Organization** - Organize transactions with customizable categories

### Technical Highlights
- **JWT Authentication** - Secure token-based auth with refresh token rotation
- **RESTful API** - Well-structured endpoints following REST principles
- **Type Safety** - TypeScript on frontend, Java with validation annotations on backend
- **Responsive Design** - Mobile-first UI that works seamlessly across devices
- **Data Validation** - Request validation with Bean Validation API on backend, mirrored client-side
- **Security** - CORS configuration, BCrypt password storage, rate-limited auth endpoints

## Getting Started

### Prerequisites
- Node.js 20+ and pnpm
- Java 21+
- Docker and Docker Compose (optional, for database)

### Local Development

1. **Clone the repository**
```bash
git clone https://github.com/atin-roy/ledgerly.git
cd ledgerly
```

2. **Start the database**
```bash
docker-compose up -d
```

3. **Run the backend**
```bash
cd backend
./gradlew bootRun
```
The API will be available at `http://localhost:8080`

4. **Run the frontend**
```bash
cd frontend
pnpm install
pnpm dev
```
The application will be available at `http://localhost:3000`

### Environment Configuration

#### Backend (`backend/.env`)
```properties
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/finance
SPRING_DATASOURCE_USERNAME=dev
SPRING_DATASOURCE_PASSWORD=dev
JWT_SECRET=your-secret-key-here
```

#### Frontend (`frontend/.env.local`)
```properties
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080/api
```

## API Documentation

When the backend is running, interactive API documentation is available via Swagger UI:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs

## Project Structure

```
ledgerly/
├── backend/              # Spring Boot application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── dev/atinroy/ledgerly/
│   │   │   │       ├── controller/    # REST endpoints
│   │   │   │       ├── service/       # Business logic
│   │   │   │       ├── repository/    # Data access
│   │   │   │       ├── entity/        # JPA entities
│   │   │   │       ├── dto/           # Data transfer objects
│   │   │   │       ├── security/      # Auth config & JWT
│   │   │   │       └── config/        # Spring configuration
│   │   │   └── resources/
│   │   └── test/                      # Unit & integration tests
│   └── build.gradle.kts
├── frontend/             # Next.js application
│   ├── app/             # App Router pages
│   ├── components/      # React components
│   ├── lib/             # Utilities & API client
│   └── public/          # Static assets
├── ledgerly-api/        # Bruno API collection
└── docker-compose.yml   # PostgreSQL container
```

## Architecture Decisions

### Backend Design
- **Layered Architecture**: Clear separation between controller, service, and repository layers
- **DTO Pattern**: Decoupling internal entities from API contracts for flexibility
- **Service Layer**: Business logic isolated from HTTP concerns for testability
- **Repository Pattern**: Data access abstraction with Spring Data JPA
- **Exception Handling**: Centralized error handling with meaningful HTTP status codes

### Frontend Design
- **Client-rendered dashboard**: every authenticated route is a client component that fetches via the browser API client; no Server Component performs data fetching today, which is a deliberate trade-off for a simple SPA-shaped auth model rather than a Server Component claim
- **API Client**: Centralized fetch wrapper with error handling and token refresh
- **Component Organization**: Feature-based folder structure for scalability

### Security
- **Password Hashing**: BCrypt with configurable strength
- **JWT Tokens**: Short-lived access tokens with refresh token rotation
- **CORS**: Configured for specific origins in production
- **Input Validation**: Backend validation with Jakarta Validation API
- **SQL Injection Prevention**: Parameterized queries via JPA

## Testing

### Backend Tests
```bash
cd backend
./gradlew test
./gradlew jacocoTestReport  # Generate coverage report
```

### API Testing
Bruno API collection is available in `ledgerly-api/` with pre-configured requests for all endpoints.

## Deployment

Live at [ledgerly.atinroy.com](https://ledgerly.atinroy.com). Deployed as Docker images (GitHub Actions → GHCR) to a VPS behind Caddy, with Postgres in a sibling container — see [DEPLOYMENT.md](./DEPLOYMENT.md) for the full pipeline and layout.

### Build for Production

**Backend**:
```bash
./gradlew bootJar
# Output: backend/build/libs/app.jar
```

**Frontend**:
```bash
pnpm build
```

## License

This project is open source and available under the MIT License.

## Acknowledgments

- Design inspiration from Frontend Mentor challenges
- Icons from Lucide React
- UI components built with shadcn/ui and Radix UI
