# Multi-stage Dockerfile for Ledgerly Backend

# Stage 1: Build
FROM eclipse-temurin:21-jdk-alpine as builder

WORKDIR /build

# Copy gradle wrapper
COPY backend/gradlew .
COPY backend/gradle ./gradle
COPY backend/.gradle ./.gradle

# Copy source code
COPY backend/src ./src
COPY backend/build.gradle.kts .
COPY backend/settings.gradle.kts .

# Build the application
RUN ./gradlew clean build -x test --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the built jar from builder stage
COPY --from=builder /build/build/libs/app.jar .

# Create non-root user for security
RUN addgroup -g 1000 ledgerly && adduser -D -u 1000 -G ledgerly ledgerly
USER ledgerly

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Expose port
EXPOSE 8080

# Start application
CMD ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
