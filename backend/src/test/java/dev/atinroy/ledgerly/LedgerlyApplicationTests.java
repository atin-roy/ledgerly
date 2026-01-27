package dev.atinroy.ledgerly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Integration test for the Spring Boot application context.
 *
 * This test verifies that the entire Spring application context
 * can be loaded successfully, including:
 * - All beans are properly configured
 * - All dependencies are satisfied
 * - Configuration properties are valid
 *
 * Uses H2 in-memory database for testing (configured via test properties).
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=PostgreSQL",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
    "jwt.secret=test-secret-key-for-jwt-testing-must-be-at-least-32-bytes-long",
    "jwt.access-token-expiration=3600000",
    "jwt.refresh-token-expiration=86400000"
})
@DisplayName("Application Context Integration Tests")
class LedgerlyApplicationTests {

    /**
     * Verifies that the Spring application context loads successfully.
     *
     * This is a smoke test that catches configuration errors such as:
     * - Missing bean definitions
     * - Circular dependencies
     * - Invalid property configurations
     * - Database connection issues (uses H2 for tests)
     */
    @Test
    @DisplayName("Application context should load successfully")
    void contextLoads() {
        // If this test passes, the Spring context started without errors.
        // No assertions needed - the test fails if context loading throws.
    }

}
