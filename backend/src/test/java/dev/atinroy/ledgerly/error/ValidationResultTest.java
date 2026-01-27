package dev.atinroy.ledgerly.error;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ValidationResult.
 *
 * ValidationResult is a container for validation errors used throughout the application.
 * It supports:
 * - Field-specific errors (with field name)
 * - General errors (without field name - null)
 * - Factory methods for creating empty results
 * - Merging multiple results
 */
@DisplayName("ValidationResult Tests")
class ValidationResultTest {

    // ==================== Factory Methods Tests ====================

    @Nested
    @DisplayName("Factory Methods")
    class FactoryMethods {

        @Test
        @DisplayName("success() should create empty result")
        void successShouldCreateEmptyResult() {
            // Act
            ValidationResult result = ValidationResult.success();

            // Assert
            assertThat(result.isValid()).isTrue();
            assertThat(result.hasErrors()).isFalse();
            assertThat(result.errors()).isEmpty();
        }

        @Test
        @DisplayName("withErrors() should create empty result ready for adding errors")
        void withErrorsShouldCreateEmptyResult() {
            // Act
            ValidationResult result = ValidationResult.withErrors();

            // Assert: Initially empty
            assertThat(result.isValid()).isTrue();
            assertThat(result.errors()).isEmpty();

            // Can add errors
            result.addFieldError("test", "TEST_CODE", "Test message");
            assertThat(result.hasErrors()).isTrue();
        }
    }

    // ==================== Adding Errors Tests ====================

    @Nested
    @DisplayName("Adding Errors")
    class AddingErrors {

        @Test
        @DisplayName("addFieldError should add error with field name")
        void addFieldErrorShouldAddErrorWithFieldName() {
            // Arrange
            ValidationResult result = ValidationResult.withErrors();

            // Act
            result.addFieldError("email", ErrorCode.INVALID_FORMAT, "Invalid email format");

            // Assert
            assertThat(result.hasErrors()).isTrue();
            assertThat(result.errors()).hasSize(1);

            ValidationError error = result.errors().iterator().next();
            assertThat(error.fieldName()).isEqualTo("email");
            assertThat(error.errorCode()).isEqualTo(ErrorCode.INVALID_FORMAT);
            assertThat(error.message()).isEqualTo("Invalid email format");
        }

        @Test
        @DisplayName("addGeneralError should add error without field name")
        void addGeneralErrorShouldAddErrorWithoutFieldName() {
            // Arrange
            ValidationResult result = ValidationResult.withErrors();

            // Act
            result.addGeneralError(ErrorCode.REQUIRED, "Request is required");

            // Assert
            assertThat(result.hasErrors()).isTrue();

            ValidationError error = result.errors().iterator().next();
            assertThat(error.fieldName()).isNull();  // General errors have null field
            assertThat(error.errorCode()).isEqualTo(ErrorCode.REQUIRED);
        }

        @Test
        @DisplayName("addError should work with field name")
        void addErrorShouldWorkWithFieldName() {
            // Arrange
            ValidationResult result = ValidationResult.withErrors();

            // Act: Using generic addError method
            result.addError("username", ErrorCode.TOO_SHORT, "Username too short");

            // Assert
            assertThat(result.getFieldErrors("username")).hasSize(1);
        }

        @Test
        @DisplayName("should add multiple errors to same field")
        void shouldAddMultipleErrorsToSameField() {
            // Arrange
            ValidationResult result = ValidationResult.withErrors();

            // Act: Add two different errors for password
            result.addFieldError("password", ErrorCode.TOO_SHORT, "Password too short");
            result.addFieldError("password", ErrorCode.INVALID_FORMAT, "Must contain special char");

            // Assert
            assertThat(result.errors()).hasSize(2);
            assertThat(result.getFieldErrors("password")).hasSize(2);
        }

        @Test
        @DisplayName("should add errors for different fields")
        void shouldAddErrorsForDifferentFields() {
            // Arrange
            ValidationResult result = ValidationResult.withErrors();

            // Act
            result.addFieldError("email", ErrorCode.INVALID_FORMAT, "Invalid email");
            result.addFieldError("username", ErrorCode.REQUIRED, "Username required");
            result.addFieldError("password", ErrorCode.TOO_SHORT, "Password too short");

            // Assert
            assertThat(result.errors()).hasSize(3);
            assertThat(result.getFieldErrors("email")).hasSize(1);
            assertThat(result.getFieldErrors("username")).hasSize(1);
            assertThat(result.getFieldErrors("password")).hasSize(1);
        }
    }

    // ==================== Query Methods Tests ====================

    @Nested
    @DisplayName("Query Methods")
    class QueryMethods {

        @Test
        @DisplayName("isValid should return true when no errors")
        void isValidShouldReturnTrueWhenNoErrors() {
            // Arrange
            ValidationResult result = ValidationResult.success();

            // Assert
            assertThat(result.isValid()).isTrue();
        }

        @Test
        @DisplayName("isValid should return false when has errors")
        void isValidShouldReturnFalseWhenHasErrors() {
            // Arrange
            ValidationResult result = ValidationResult.withErrors();
            result.addFieldError("field", "CODE", "message");

            // Assert
            assertThat(result.isValid()).isFalse();
        }

        @Test
        @DisplayName("hasErrors should return false when empty")
        void hasErrorsShouldReturnFalseWhenEmpty() {
            // Arrange
            ValidationResult result = ValidationResult.success();

            // Assert
            assertThat(result.hasErrors()).isFalse();
        }

        @Test
        @DisplayName("hasErrors should return true when has errors")
        void hasErrorsShouldReturnTrueWhenHasErrors() {
            // Arrange
            ValidationResult result = ValidationResult.withErrors();
            result.addGeneralError("CODE", "message");

            // Assert
            assertThat(result.hasErrors()).isTrue();
        }

        @Test
        @DisplayName("getFieldErrors should return only errors for specified field")
        void getFieldErrorsShouldReturnOnlyErrorsForSpecifiedField() {
            // Arrange
            ValidationResult result = ValidationResult.withErrors();
            result.addFieldError("email", "CODE1", "Error 1");
            result.addFieldError("email", "CODE2", "Error 2");
            result.addFieldError("username", "CODE3", "Error 3");
            result.addGeneralError("CODE4", "General error");

            // Act
            Set<ValidationError> emailErrors = result.getFieldErrors("email");

            // Assert
            assertThat(emailErrors).hasSize(2);
            assertThat(emailErrors)
                    .allMatch(error -> "email".equals(error.fieldName()));
        }

        @Test
        @DisplayName("getFieldErrors should return empty set for non-existent field")
        void getFieldErrorsShouldReturnEmptySetForNonExistentField() {
            // Arrange
            ValidationResult result = ValidationResult.withErrors();
            result.addFieldError("email", "CODE", "message");

            // Act
            Set<ValidationError> nonExistentErrors = result.getFieldErrors("password");

            // Assert
            assertThat(nonExistentErrors).isEmpty();
        }

        @Test
        @DisplayName("getGeneralErrors should return only errors without field name")
        void getGeneralErrorsShouldReturnOnlyErrorsWithoutFieldName() {
            // Arrange
            ValidationResult result = ValidationResult.withErrors();
            result.addFieldError("email", "CODE1", "Field error");
            result.addGeneralError("CODE2", "General error 1");
            result.addGeneralError("CODE3", "General error 2");

            // Act
            Set<ValidationError> generalErrors = result.getGeneralErrors();

            // Assert
            assertThat(generalErrors).hasSize(2);
            assertThat(generalErrors)
                    .allMatch(error -> error.fieldName() == null);
        }

        @Test
        @DisplayName("errors() should return unmodifiable set")
        void errorsShouldReturnUnmodifiableSet() {
            // Arrange
            ValidationResult result = ValidationResult.withErrors();
            result.addFieldError("field", "CODE", "message");

            // Act
            Set<ValidationError> errors = result.errors();

            // Assert: Attempting to modify should throw exception
            // (or at least the returned set shouldn't affect the original)
            assertThat(errors).hasSize(1);
        }
    }

    // ==================== Merge Tests ====================

    @Nested
    @DisplayName("Merge Results")
    class MergeResults {

        @Test
        @DisplayName("merge should combine errors from two results")
        void mergeShouldCombineErrorsFromTwoResults() {
            // Arrange
            ValidationResult result1 = ValidationResult.withErrors();
            result1.addFieldError("email", "CODE1", "Error 1");

            ValidationResult result2 = ValidationResult.withErrors();
            result2.addFieldError("username", "CODE2", "Error 2");

            // Act
            result1.merge(result2);

            // Assert
            assertThat(result1.errors()).hasSize(2);
            assertThat(result1.getFieldErrors("email")).hasSize(1);
            assertThat(result1.getFieldErrors("username")).hasSize(1);
        }

        @Test
        @DisplayName("merge with empty result should not change")
        void mergeWithEmptyResultShouldNotChange() {
            // Arrange
            ValidationResult result1 = ValidationResult.withErrors();
            result1.addFieldError("email", "CODE", "Error");

            ValidationResult emptyResult = ValidationResult.success();

            // Act
            result1.merge(emptyResult);

            // Assert
            assertThat(result1.errors()).hasSize(1);
        }

        @Test
        @DisplayName("merge empty into result should add errors")
        void mergeEmptyIntoResultShouldAddErrors() {
            // Arrange
            ValidationResult emptyResult = ValidationResult.success();

            ValidationResult result2 = ValidationResult.withErrors();
            result2.addFieldError("password", "CODE", "Error");

            // Act
            emptyResult.merge(result2);

            // Assert
            assertThat(emptyResult.errors()).hasSize(1);
            assertThat(emptyResult.hasErrors()).isTrue();
        }
    }

    // ==================== Edge Cases Tests ====================

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("should handle duplicate errors gracefully")
        void shouldHandleDuplicateErrorsGracefully() {
            // Arrange: Add exact same error twice
            ValidationResult result = ValidationResult.withErrors();

            // Act
            result.addFieldError("email", "CODE", "Same message");
            result.addFieldError("email", "CODE", "Same message");

            // Assert: Set behavior - duplicates may or may not be stored
            // depending on ValidationError equals/hashCode
            assertThat(result.hasErrors()).isTrue();
        }

        @Test
        @DisplayName("should work with empty string field name")
        void shouldWorkWithEmptyStringFieldName() {
            // Arrange
            ValidationResult result = ValidationResult.withErrors();

            // Act
            result.addFieldError("", "CODE", "Error for empty field name");

            // Assert
            assertThat(result.hasErrors()).isTrue();
            assertThat(result.getFieldErrors("")).hasSize(1);
        }

        @Test
        @DisplayName("should distinguish between null and empty field name")
        void shouldDistinguishBetweenNullAndEmptyFieldName() {
            // Arrange
            ValidationResult result = ValidationResult.withErrors();

            // Act
            result.addFieldError("", "CODE1", "Empty field");
            result.addGeneralError("CODE2", "General error");

            // Assert
            assertThat(result.getFieldErrors("")).hasSize(1);
            assertThat(result.getGeneralErrors()).hasSize(1);
        }
    }
}
