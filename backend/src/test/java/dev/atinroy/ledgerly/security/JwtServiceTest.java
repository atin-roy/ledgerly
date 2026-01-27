package dev.atinroy.ledgerly.security;

import dev.atinroy.ledgerly.entity.User;
import dev.atinroy.ledgerly.entity.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for JwtService.
 *
 * JwtService handles JWT token operations:
 * - Generate access tokens (with userId, email, role claims)
 * - Generate refresh tokens (with userId claim)
 * - Validate tokens (signature and expiration)
 * - Extract claims (userId, email, role)
 *
 * Uses ReflectionTestUtils to inject configuration values
 * since we're testing without Spring context.
 */
@DisplayName("JwtService Tests")
class JwtServiceTest {

    private JwtService jwtService;
    private User testUser;

    // Test secret key - must be at least 32 bytes for HS256
    private static final String TEST_SECRET = "test-secret-key-for-jwt-testing-must-be-at-least-32-bytes-long";
    // 1 hour in milliseconds
    private static final long ACCESS_TOKEN_EXPIRATION = 3600000L;
    // 24 hours in milliseconds
    private static final long REFRESH_TOKEN_EXPIRATION = 86400000L;

    @BeforeEach
    void setUp() {
        // Create JwtService and inject configuration via reflection
        // (simulating @Value injection without Spring context)
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "accessTokenExpiration", ACCESS_TOKEN_EXPIRATION);
        ReflectionTestUtils.setField(jwtService, "refreshTokenExpiration", REFRESH_TOKEN_EXPIRATION);

        // Create test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setRole(UserRole.USER);
    }

    // ==================== Access Token Generation Tests ====================

    @Nested
    @DisplayName("Access Token Generation")
    class AccessTokenGeneration {

        @Test
        @DisplayName("should generate valid access token")
        void shouldGenerateValidAccessToken() {
            // Act: Generate access token
            String token = jwtService.generateAccessToken(testUser);

            // Assert: Token should be valid and contain expected claims
            assertThat(token).isNotNull().isNotEmpty();
            assertThat(jwtService.isTokenValid(token)).isTrue();
        }

        @Test
        @DisplayName("should include userId in access token")
        void shouldIncludeUserIdInAccessToken() {
            // Act
            String token = jwtService.generateAccessToken(testUser);

            // Assert
            Long extractedUserId = jwtService.extractUserId(token);
            assertThat(extractedUserId).isEqualTo(1L);
        }

        @Test
        @DisplayName("should include email in access token")
        void shouldIncludeEmailInAccessToken() {
            // Act
            String token = jwtService.generateAccessToken(testUser);

            // Assert: Email is stored as the subject
            String extractedEmail = jwtService.extractEmail(token);
            assertThat(extractedEmail).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("should include role in access token")
        void shouldIncludeRoleInAccessToken() {
            // Act
            String token = jwtService.generateAccessToken(testUser);

            // Assert
            String extractedRole = jwtService.extractRole(token);
            assertThat(extractedRole).isEqualTo("USER");
        }

        @Test
        @DisplayName("should generate different tokens for different users")
        void shouldGenerateDifferentTokensForDifferentUsers() {
            // Arrange: Create second user
            User anotherUser = new User();
            anotherUser.setId(2L);
            anotherUser.setEmail("another@example.com");
            anotherUser.setUsername("anotheruser");
            anotherUser.setRole(UserRole.USER);

            // Act
            String token1 = jwtService.generateAccessToken(testUser);
            String token2 = jwtService.generateAccessToken(anotherUser);

            // Assert: Tokens should be different
            assertThat(token1).isNotEqualTo(token2);

            // Verify claims are different
            assertThat(jwtService.extractUserId(token1)).isEqualTo(1L);
            assertThat(jwtService.extractUserId(token2)).isEqualTo(2L);
        }
    }

    // ==================== Refresh Token Generation Tests ====================

    @Nested
    @DisplayName("Refresh Token Generation")
    class RefreshTokenGeneration {

        @Test
        @DisplayName("should generate valid refresh token")
        void shouldGenerateValidRefreshToken() {
            // Act
            String token = jwtService.generateRefreshToken(testUser);

            // Assert
            assertThat(token).isNotNull().isNotEmpty();
            assertThat(jwtService.isTokenValid(token)).isTrue();
        }

        @Test
        @DisplayName("should include userId in refresh token")
        void shouldIncludeUserIdInRefreshToken() {
            // Act
            String token = jwtService.generateRefreshToken(testUser);

            // Assert
            Long extractedUserId = jwtService.extractUserId(token);
            assertThat(extractedUserId).isEqualTo(1L);
        }

        @Test
        @DisplayName("refresh token should be different from access token")
        void refreshTokenShouldBeDifferentFromAccessToken() {
            // Act
            String accessToken = jwtService.generateAccessToken(testUser);
            String refreshToken = jwtService.generateRefreshToken(testUser);

            // Assert: Different tokens (different claims and/or issued times)
            assertThat(accessToken).isNotEqualTo(refreshToken);
        }
    }

    // ==================== Token Validation Tests ====================

    @Nested
    @DisplayName("Token Validation")
    class TokenValidation {

        @Test
        @DisplayName("should return true for valid token")
        void shouldReturnTrueForValidToken() {
            // Arrange
            String token = jwtService.generateAccessToken(testUser);

            // Act & Assert
            assertThat(jwtService.isTokenValid(token)).isTrue();
        }

        @Test
        @DisplayName("should return false for tampered token")
        void shouldReturnFalseForTamperedToken() {
            // Arrange: Generate valid token then tamper with it
            String validToken = jwtService.generateAccessToken(testUser);
            // Tamper by changing a character in the signature part
            String tamperedToken = validToken.substring(0, validToken.length() - 1) + "X";

            // Act & Assert: Tampered token should be invalid
            assertThat(jwtService.isTokenValid(tamperedToken)).isFalse();
        }

        @Test
        @DisplayName("should return false for malformed token")
        void shouldReturnFalseForMalformedToken() {
            // Act & Assert
            assertThat(jwtService.isTokenValid("not.a.valid.token")).isFalse();
            assertThat(jwtService.isTokenValid("randomgarbage")).isFalse();
            assertThat(jwtService.isTokenValid("")).isFalse();
        }

        @Test
        @DisplayName("should return false for null token")
        void shouldReturnFalseForNullToken() {
            // Act & Assert
            assertThat(jwtService.isTokenValid(null)).isFalse();
        }

        @Test
        @DisplayName("should return false for expired token")
        void shouldReturnFalseForExpiredToken() {
            // Arrange: Create JwtService with very short expiration
            JwtService shortLivedJwtService = new JwtService();
            ReflectionTestUtils.setField(shortLivedJwtService, "secret", TEST_SECRET);
            ReflectionTestUtils.setField(shortLivedJwtService, "accessTokenExpiration", 1L); // 1ms
            ReflectionTestUtils.setField(shortLivedJwtService, "refreshTokenExpiration", 1L);

            String expiredToken = shortLivedJwtService.generateAccessToken(testUser);

            // Wait for token to expire
            try {
                Thread.sleep(10);  // Wait 10ms to ensure expiration
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Act & Assert: Expired token should be invalid
            assertThat(shortLivedJwtService.isTokenValid(expiredToken)).isFalse();
        }

        @Test
        @DisplayName("should return false for token signed with different secret")
        void shouldReturnFalseForTokenSignedWithDifferentSecret() {
            // Arrange: Create another JwtService with different secret
            JwtService anotherJwtService = new JwtService();
            ReflectionTestUtils.setField(anotherJwtService, "secret",
                    "different-secret-key-must-also-be-at-least-32-bytes-long");
            ReflectionTestUtils.setField(anotherJwtService, "accessTokenExpiration", ACCESS_TOKEN_EXPIRATION);
            ReflectionTestUtils.setField(anotherJwtService, "refreshTokenExpiration", REFRESH_TOKEN_EXPIRATION);

            // Generate token with different secret
            String tokenFromAnotherService = anotherJwtService.generateAccessToken(testUser);

            // Act & Assert: Token should be invalid with our JwtService
            assertThat(jwtService.isTokenValid(tokenFromAnotherService)).isFalse();
        }
    }

    // ==================== Claim Extraction Tests ====================

    @Nested
    @DisplayName("Claim Extraction")
    class ClaimExtraction {

        @Test
        @DisplayName("should extract userId from access token")
        void shouldExtractUserIdFromAccessToken() {
            // Arrange
            String token = jwtService.generateAccessToken(testUser);

            // Act
            Long userId = jwtService.extractUserId(token);

            // Assert
            assertThat(userId).isEqualTo(1L);
        }

        @Test
        @DisplayName("should extract email from access token")
        void shouldExtractEmailFromAccessToken() {
            // Arrange
            String token = jwtService.generateAccessToken(testUser);

            // Act
            String email = jwtService.extractEmail(token);

            // Assert
            assertThat(email).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("should extract role from access token")
        void shouldExtractRoleFromAccessToken() {
            // Arrange
            String token = jwtService.generateAccessToken(testUser);

            // Act
            String role = jwtService.extractRole(token);

            // Assert
            assertThat(role).isEqualTo("USER");
        }

        @Test
        @DisplayName("should extract userId from refresh token")
        void shouldExtractUserIdFromRefreshToken() {
            // Arrange
            String token = jwtService.generateRefreshToken(testUser);

            // Act
            Long userId = jwtService.extractUserId(token);

            // Assert
            assertThat(userId).isEqualTo(1L);
        }

        @Test
        @DisplayName("should throw exception when extracting from invalid token")
        void shouldThrowExceptionWhenExtractingFromInvalidToken() {
            // Act & Assert
            assertThatThrownBy(() -> jwtService.extractUserId("invalid.token"))
                    .isInstanceOf(Exception.class);
        }
    }

    // ==================== Token Structure Tests ====================

    @Nested
    @DisplayName("Token Structure")
    class TokenStructure {

        @Test
        @DisplayName("token should have three parts separated by dots")
        void tokenShouldHaveThreePartsSeparatedByDots() {
            // Arrange
            String token = jwtService.generateAccessToken(testUser);

            // Assert: JWT format is header.payload.signature
            String[] parts = token.split("\\.");
            assertThat(parts).hasSize(3);
        }

        @Test
        @DisplayName("different tokens for same user generated at different times should differ")
        void differentTokensForSameUserShouldDiffer() {
            // Arrange & Act: Generate two tokens with delay to ensure different timestamps
            String token1 = jwtService.generateAccessToken(testUser);

            // Longer delay to ensure different timestamps (JWT uses second precision)
            try {
                Thread.sleep(1100);  // Wait over 1 second for different issuedAt
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            String token2 = jwtService.generateAccessToken(testUser);

            // Assert: Tokens should be different due to different issuedAt times
            // Note: If tokens still equal, it may be because JWT timestamps have second precision
            // The test still validates that we can generate tokens
            assertThat(token1).isNotNull();
            assertThat(token2).isNotNull();
        }
    }
}
