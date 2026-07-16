# Stage 1: Build
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build

# Gradle files first — this layer (and the dependency download below) is
# cached until build.gradle.kts or the wrapper changes
COPY gradlew build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle
RUN ./gradlew dependencies --no-daemon > /dev/null

# Source changes only invalidate from here down
COPY src ./src
RUN ./gradlew bootJar -x test --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /build/build/libs/app.jar .

RUN addgroup -g 1000 ledgerly && adduser -D -u 1000 -G ledgerly ledgerly
USER ledgerly

HEALTHCHECK --interval=30s --timeout=10s --start-period=20s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

EXPOSE 8080

CMD ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
