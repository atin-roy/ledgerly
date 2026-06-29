package dev.atinroy.ledgerly.domain.user.validator;

import dev.atinroy.ledgerly.domain.user.dto.UserCreateRequest;
import dev.atinroy.ledgerly.domain.user.dto.UserUpdateRequest;
import dev.atinroy.ledgerly.shared.error.ErrorCode;
import dev.atinroy.ledgerly.shared.error.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for UserValidator.
 *
 * These tests verify the validation logic for user creation and update requests.
 * The UserValidator checks:
 * - Email format (must contain @ and .)
 * - Username presence (required)
 * - Password length (minimum 8 characters)
 */
@DisplayName("UserValidator Tests")
class UserValidatorTest {

    // The validator instance we're testing
    private UserValidator userValidator;

    @BeforeEach
    void setUp() {
        // Create a fresh validator before each test
        userValidator = new UserValidator();
    }

    // ==================== UserCreateRequest Validation Tests ====================

    @Nested
    @DisplayName("UserCreateRequest Validation")
    class UserCreateRequestValidation {

        @Test
        @DisplayName("should pass validation with valid user data")
        void shouldPassValidationWithValidUserData() {
            // Arrange: Create a valid user request with all required fields
            UserCreateRequest request = new UserCreateRequest(
                    "test@example.com",  // Valid email with @ and .
                    "testuser",          // Valid username
                    "password123"        // Valid password (8+ chars)
            );

            // Act: Run validation
            ValidationResult result = userValidator.validate(request);

            // Assert: No errors should be present
            assertThat(result.isValid()).isTrue();
            assertThat(result.hasErrors()).isFalse();
        }

        @Test
        @DisplayName("should fail validation when request is null")
        void shouldFailValidationWhenRequestIsNull() {
            // Act: Validate a null request
            ValidationResult result = userValidator.validate((UserCreateRequest) null);

            // Assert: Should have a general error indicating request is required
            assertThat(result.hasErrors()).isTrue();
            assertThat(result.getGeneralErrors())
                    .anyMatch(error -> error.errorCode().equals(ErrorCode.REQUIRED));
        }

        @Test
        @DisplayName("should fail validation when email is null")
        void shouldFailValidationWhenEmailIsNull() {
            // Arrange: Request with null email
            UserCreateRequest request = new UserCreateRequest(
                    null,           // Missing email
                    "testuser",
                    "password123"
            );

            // Act
            ValidationResult result = userValidator.validate(request);

            // Assert: Should have email field error
            assertThat(result.hasErrors()).isTrue();
            assertThat(result.getFieldErrors("email"))
                    .anyMatch(error -> error.errorCode().equals(ErrorCode.REQUIRED));
        }

        @Test
        @DisplayName("should fail validation when email is blank")
        void shouldFailValidationWhenEmailIsBlank() {
            // Arrange: Request with blank email (whitespace only)
            UserCreateRequest request = new UserCreateRequest(
                    "   ",          // Blank email
                    "testuser",
                    "password123"
            );

            // Act
            ValidationResult result = userValidator.validate(request);

            // Assert: Should have email field error
            assertThat(result.hasErrors()).isTrue();
            assertThat(result.getFieldErrors("email")).isNotEmpty();
        }

        @Test
        @DisplayName("should fail validation when email is missing @ symbol")
        void shouldFailValidationWhenEmailMissingAtSymbol() {
            // Arrange: Email without @ symbol
            UserCreateRequest request = new UserCreateRequest(
                    "testexample.com",  // Missing @
                    "testuser",
                    "password123"
            );

            // Act
            ValidationResult result = userValidator.validate(request);

            // Assert: Should have invalid format error
            assertThat(result.hasErrors()).isTrue();
            assertThat(result.getFieldErrors("email"))
                    .anyMatch(error -> error.errorCode().equals(ErrorCode.INVALID_FORMAT));
        }

        @Test
        @DisplayName("should fail validation when email is missing dot")
        void shouldFailValidationWhenEmailMissingDot() {
            // Arrange: Email without dot (.)
            UserCreateRequest request = new UserCreateRequest(
                    "test@examplecom",  // Missing .
                    "testuser",
                    "password123"
            );

            // Act
            ValidationResult result = userValidator.validate(request);

            // Assert: Should have invalid format error
            assertThat(result.hasErrors()).isTrue();
            assertThat(result.getFieldErrors("email"))
                    .anyMatch(error -> error.errorCode().equals(ErrorCode.INVALID_FORMAT));
        }

        @Test
        @DisplayName("should fail validation when username is null")
        void shouldFailValidationWhenUsernameIsNull() {
            // Arrange: Request with null username
            UserCreateRequest request = new UserCreateRequest(
                    "test@example.com",
                    null,           // Missing username
                    "password123"
            );

            // Act
            ValidationResult result = userValidator.validate(request);

            // Assert: Should have username field error
            assertThat(result.hasErrors()).isTrue();
            assertThat(result.getFieldErrors("username"))
                    .anyMatch(error -> error.errorCode().equals(ErrorCode.REQUIRED));
        }

        @Test
        @DisplayName("should fail validation when username is blank")
        void shouldFailValidationWhenUsernameIsBlank() {
            // Arrange: Request with blank username
            UserCreateRequest request = new UserCreateRequest(
                    "test@example.com",
                    "   ",          // Blank username
                    "password123"
            );

            // Act
            ValidationResult result = userValidator.validate(request);

            // Assert: Should have username field error
            assertThat(result.hasErrors()).isTrue();
            assertThat(result.getFieldErrors("username")).isNotEmpty();
        }

        @Test
        @DisplayName("should fail validation when password is null")
        void shouldFailValidationWhenPasswordIsNull() {
            // Arrange: Request with null password
            UserCreateRequest request = new UserCreateRequest(
                    "test@example.com",
                    "testuser",
                    null            // Missing password
            );

            // Act
            ValidationResult result = userValidator.validate(request);

            // Assert: Should have password field error
            assertThat(result.hasErrors()).isTrue();
            assertThat(result.getFieldErrors("password"))
                    .anyMatch(error -> error.errorCode().equals(ErrorCode.TOO_SHORT));
        }

        @Test
        @DisplayName("should fail validation when password is too short")
        void shouldFailValidationWhenPasswordIsTooShort() {
            // Arrange: Request with password less than 8 characters
            UserCreateRequest request = new UserCreateRequest(
                    "test@example.com",
                    "testuser",
                    "pass"          // Only 4 characters (min is 8)
            );

            // Act
            ValidationResult result = userValidator.validate(request);

            // Assert: Should have password too short error
            assertThat(result.hasErrors()).isTrue();
            assertThat(result.getFieldErrors("password"))
                    .anyMatch(error -> error.errorCode().equals(ErrorCode.TOO_SHORT));
        }

        @Test
        @DisplayName("should pass validation with exactly 8 character password")
        void shouldPassValidationWithExactly8CharacterPassword() {
            // Arrange: Password with exactly 8 characters (minimum)
            UserCreateRequest request = new UserCreateRequest(
                    "test@example.com",
                    "testuser",
                    "12345678"      // Exactly 8 characters
            );

            // Act
            ValidationResult result = userValidator.validate(request);

            // Assert: Should pass validation
            assertThat(result.isValid()).isTrue();
        }

        @Test
        @DisplayName("should collect multiple validation errors")
        void shouldCollectMultipleValidationErrors() {
            // Arrange: Request with multiple invalid fields
            UserCreateRequest request = new UserCreateRequest(
                    "invalid-email",  // Invalid email format
                    "",               // Empty username
                    "short"           // Too short password
            );

            // Act
            ValidationResult result = userValidator.validate(request);

            // Assert: Should have errors for all three fields
            assertThat(result.hasErrors()).isTrue();
            assertThat(result.getFieldErrors("email")).isNotEmpty();
            assertThat(result.getFieldErrors("username")).isNotEmpty();
            assertThat(result.getFieldErrors("password")).isNotEmpty();
        }
    }

    // ==================== UserUpdateRequest Validation Tests ====================

    @Nested
    @DisplayName("UserUpdateRequest Validation")
    class UserUpdateRequestValidation {

        @Test
        @DisplayName("should pass validation with all null fields (all fields optional)")
        void shouldPassValidationWithAllNullFields() {
            // Arrange: Update request with all null fields
            // In updates, all fields are optional - only provided fields are validated
            UserUpdateRequest request = new UserUpdateRequest(null, null, null);

            // Act
            ValidationResult result = userValidator.validate(request);

            // Assert: Should pass because all fields are optional in updates
            assertThat(result.isValid()).isTrue();
        }

        @Test
        @DisplayName("should pass validation with valid email only")
        void shouldPassValidationWithValidEmailOnly() {
            // Arrange: Only email is being updated
            UserUpdateRequest request = new UserUpdateRequest(
                    "newemail@example.com",  // Valid new email
                    null,                     // Not updating username
                    null                      // Not updating password
            );

            // Act
            ValidationResult result = userValidator.validate(request);

            // Assert
            assertThat(result.isValid()).isTrue();
        }

        @Test
        @DisplayName("should fail validation when provided email is invalid")
        void shouldFailValidationWhenProvidedEmailIsInvalid() {
            // Arrange: Invalid email format in update
            UserUpdateRequest request = new UserUpdateRequest(
                    "invalid-email",  // Invalid format
                    null,
                    null
            );

            // Act
            ValidationResult result = userValidator.validate(request);

            // Assert: Should fail because provided email is invalid
            assertThat(result.hasErrors()).isTrue();
            assertThat(result.getFieldErrors("email"))
                    .anyMatch(error -> error.errorCode().equals(ErrorCode.INVALID_FORMAT));
        }

        @Test
        @DisplayName("should fail validation when provided password is too short")
        void shouldFailValidationWhenProvidedPasswordIsTooShort() {
            // Arrange: Password update with too short password
            UserUpdateRequest request = new UserUpdateRequest(
                    null,
                    null,
                    "short"  // Less than 8 characters
            );

            // Act
            ValidationResult result = userValidator.validate(request);

            // Assert
            assertThat(result.hasErrors()).isTrue();
            assertThat(result.getFieldErrors("password"))
                    .anyMatch(error -> error.errorCode().equals(ErrorCode.TOO_SHORT));
        }

        @Test
        @DisplayName("should fail validation when request is null")
        void shouldFailValidationWhenRequestIsNull() {
            // Act
            ValidationResult result = userValidator.validate((UserUpdateRequest) null);

            // Assert
            assertThat(result.hasErrors()).isTrue();
            assertThat(result.getGeneralErrors())
                    .anyMatch(error -> error.errorCode().equals(ErrorCode.REQUIRED));
        }

        @Test
        @DisplayName("should pass validation with valid update data")
        void shouldPassValidationWithValidUpdateData() {
            // Arrange: Valid update with all fields
            UserUpdateRequest request = new UserUpdateRequest(
                    "updated@example.com",
                    "updateduser",
                    "newpassword123"
            );

            // Act
            ValidationResult result = userValidator.validate(request);

            // Assert
            assertThat(result.isValid()).isTrue();
        }
    }
}
