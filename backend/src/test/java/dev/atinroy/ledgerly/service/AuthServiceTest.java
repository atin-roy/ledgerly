package dev.atinroy.ledgerly.service;

import dev.atinroy.ledgerly.dto.request.auth.LoginRequest;
import dev.atinroy.ledgerly.dto.request.auth.RefreshTokenRequest;
import dev.atinroy.ledgerly.dto.request.auth.RegisterRequest;
import dev.atinroy.ledgerly.dto.response.AuthResponse;
import dev.atinroy.ledgerly.entity.Category;
import dev.atinroy.ledgerly.entity.User;
import dev.atinroy.ledgerly.entity.enums.UserRole;
import dev.atinroy.ledgerly.error.InvalidCredentialsException;
import dev.atinroy.ledgerly.error.InvalidTokenException;
import dev.atinroy.ledgerly.error.ValidationException;
import dev.atinroy.ledgerly.repository.CategoryRepository;
import dev.atinroy.ledgerly.repository.UserRepository;
import dev.atinroy.ledgerly.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService.
 *
 * AuthService handles:
 * - User registration (with auto-creation of "General" category)
 * - User login (authentication)
 * - Token refresh
 *
 * Uses Mockito to mock dependencies (repositories, JWT service, etc.)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    // Mock all dependencies
    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    // Inject mocks into AuthService
    @InjectMocks
    private AuthService authService;

    // Test fixtures - commonly used test data
    private User testUser;

    @BeforeEach
    void setUp() {
        // Create a test user that will be used across multiple tests
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setPassword("encoded_password");
        testUser.setRole(UserRole.USER);
    }

    // ==================== Registration Tests ====================

    @Nested
    @DisplayName("User Registration")
    class UserRegistration {

        @Test
        @DisplayName("should register user successfully with valid data")
        void shouldRegisterUserSuccessfully() {
            // Arrange: Set up test data and mock behaviors
            RegisterRequest request = new RegisterRequest(
                    "newuser@example.com",
                    "newuser",
                    "password123"
            );

            // Mock: Email and username don't exist yet
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(userRepository.existsByUsername(anyString())).thenReturn(false);

            // Mock: Password encoding
            when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");

            // Mock: Save returns the user with an ID
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId(1L);
                return user;
            });

            // Mock: Category save for "General" category
            when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
                Category category = invocation.getArgument(0);
                category.setId(1L);
                return category;
            });

            // Mock: JWT token generation
            when(jwtService.generateAccessToken(any(User.class))).thenReturn("access_token");
            when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refresh_token");

            // Act: Call the registration method
            AuthResponse response = authService.register(request);

            // Assert: Verify response contains expected data
            assertThat(response).isNotNull();
            assertThat(response.accessToken()).isEqualTo("access_token");
            assertThat(response.refreshToken()).isEqualTo("refresh_token");
            assertThat(response.email()).isEqualTo("newuser@example.com");
            assertThat(response.username()).isEqualTo("newuser");
            assertThat(response.role()).isEqualTo("USER");

            // Verify: User was saved
            verify(userRepository).save(any(User.class));

            // Verify: "General" category was created for the new user
            ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
            verify(categoryRepository).save(categoryCaptor.capture());
            assertThat(categoryCaptor.getValue().getName()).isEqualTo("General");
        }

        @Test
        @DisplayName("should fail registration when email already exists")
        void shouldFailRegistrationWhenEmailExists() {
            // Arrange
            RegisterRequest request = new RegisterRequest(
                    "existing@example.com",
                    "newuser",
                    "password123"
            );

            // Mock: Email already exists
            when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);
            when(userRepository.existsByUsername(anyString())).thenReturn(false);

            // Act & Assert: Should throw ValidationException
            assertThatThrownBy(() -> authService.register(request))
                    .isInstanceOf(ValidationException.class);

            // Verify: No user was saved
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("should fail registration when username already exists")
        void shouldFailRegistrationWhenUsernameExists() {
            // Arrange
            RegisterRequest request = new RegisterRequest(
                    "newuser@example.com",
                    "existinguser",
                    "password123"
            );

            // Mock: Username already exists
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(userRepository.existsByUsername("existinguser")).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> authService.register(request))
                    .isInstanceOf(ValidationException.class);

            // Verify: No user was saved
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("should fail registration when both email and username exist")
        void shouldFailRegistrationWhenBothEmailAndUsernameExist() {
            // Arrange
            RegisterRequest request = new RegisterRequest(
                    "existing@example.com",
                    "existinguser",
                    "password123"
            );

            // Mock: Both exist
            when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);
            when(userRepository.existsByUsername("existinguser")).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> authService.register(request))
                    .isInstanceOf(ValidationException.class);
        }

        @Test
        @DisplayName("should encode password before saving")
        void shouldEncodePasswordBeforeSaving() {
            // Arrange
            RegisterRequest request = new RegisterRequest(
                    "user@example.com",
                    "user",
                    "plaintext_password"
            );

            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(userRepository.existsByUsername(anyString())).thenReturn(false);
            when(passwordEncoder.encode("plaintext_password")).thenReturn("encoded_password");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId(1L);
                return user;
            });
            when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
                Category category = invocation.getArgument(0);
                category.setId(1L);
                return category;
            });
            when(jwtService.generateAccessToken(any(User.class))).thenReturn("token");
            when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refresh");

            // Act
            authService.register(request);

            // Assert: Verify password was encoded
            verify(passwordEncoder).encode("plaintext_password");

            // Verify saved user has encoded password
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            assertThat(userCaptor.getValue().getPassword()).isEqualTo("encoded_password");
        }

        @Test
        @DisplayName("should set user role to USER")
        void shouldSetUserRoleToUser() {
            // Arrange
            RegisterRequest request = new RegisterRequest(
                    "user@example.com",
                    "user",
                    "password123"
            );

            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(userRepository.existsByUsername(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("encoded");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId(1L);
                return user;
            });
            when(categoryRepository.save(any(Category.class))).thenReturn(new Category());
            when(jwtService.generateAccessToken(any(User.class))).thenReturn("token");
            when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refresh");

            // Act
            authService.register(request);

            // Assert: Verify user role is USER
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            assertThat(userCaptor.getValue().getRole()).isEqualTo(UserRole.USER);
        }
    }

    // ==================== Login Tests ====================

    @Nested
    @DisplayName("User Login")
    class UserLogin {

        @Test
        @DisplayName("should login successfully with valid credentials")
        void shouldLoginSuccessfully() {
            // Arrange
            LoginRequest request = new LoginRequest("test@example.com", "password123");

            // Mock: Authentication succeeds
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(new UsernamePasswordAuthenticationToken("test@example.com", "password123"));

            // Mock: User lookup succeeds
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

            // Mock: Token generation
            when(jwtService.generateAccessToken(testUser)).thenReturn("access_token");
            when(jwtService.generateRefreshToken(testUser)).thenReturn("refresh_token");

            // Act
            AuthResponse response = authService.login(request);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.accessToken()).isEqualTo("access_token");
            assertThat(response.refreshToken()).isEqualTo("refresh_token");
            assertThat(response.userId()).isEqualTo(1L);
            assertThat(response.email()).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("should fail login with invalid credentials")
        void shouldFailLoginWithInvalidCredentials() {
            // Arrange
            LoginRequest request = new LoginRequest("test@example.com", "wrong_password");

            // Mock: Authentication fails
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Invalid credentials"));

            // Act & Assert
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(InvalidCredentialsException.class)
                    .hasMessage("Invalid email or password");
        }

        @Test
        @DisplayName("should fail login when user not found after authentication")
        void shouldFailLoginWhenUserNotFoundAfterAuth() {
            // Arrange: Edge case - auth succeeds but user not found
            // This shouldn't happen in practice but tests defensive coding
            LoginRequest request = new LoginRequest("ghost@example.com", "password123");

            // Mock: Auth succeeds
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(new UsernamePasswordAuthenticationToken("ghost@example.com", "password123"));

            // Mock: But user not found
            when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(InvalidCredentialsException.class);
        }
    }

    // ==================== Token Refresh Tests ====================

    @Nested
    @DisplayName("Token Refresh")
    class TokenRefresh {

        @Test
        @DisplayName("should refresh token successfully with valid refresh token")
        void shouldRefreshTokenSuccessfully() {
            // Arrange
            RefreshTokenRequest request = new RefreshTokenRequest("valid_refresh_token");

            // Mock: Token is valid
            when(jwtService.isTokenValid("valid_refresh_token")).thenReturn(true);
            when(jwtService.extractUserId("valid_refresh_token")).thenReturn(1L);

            // Mock: User exists
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            // Mock: New tokens
            when(jwtService.generateAccessToken(testUser)).thenReturn("new_access_token");
            when(jwtService.generateRefreshToken(testUser)).thenReturn("new_refresh_token");

            // Act
            AuthResponse response = authService.refreshToken(request);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.accessToken()).isEqualTo("new_access_token");
            assertThat(response.refreshToken()).isEqualTo("new_refresh_token");
        }

        @Test
        @DisplayName("should fail refresh with invalid token")
        void shouldFailRefreshWithInvalidToken() {
            // Arrange
            RefreshTokenRequest request = new RefreshTokenRequest("invalid_token");

            // Mock: Token is invalid
            when(jwtService.isTokenValid("invalid_token")).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> authService.refreshToken(request))
                    .isInstanceOf(InvalidTokenException.class)
                    .hasMessage("Invalid or expired refresh token");
        }

        @Test
        @DisplayName("should fail refresh when user not found")
        void shouldFailRefreshWhenUserNotFound() {
            // Arrange
            RefreshTokenRequest request = new RefreshTokenRequest("valid_but_user_deleted");

            // Mock: Token is valid
            when(jwtService.isTokenValid("valid_but_user_deleted")).thenReturn(true);
            when(jwtService.extractUserId("valid_but_user_deleted")).thenReturn(999L);

            // Mock: User no longer exists (deleted account)
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> authService.refreshToken(request))
                    .isInstanceOf(InvalidTokenException.class)
                    .hasMessage("User not found");
        }
    }
}
