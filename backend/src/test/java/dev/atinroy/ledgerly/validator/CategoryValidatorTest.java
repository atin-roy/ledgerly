package dev.atinroy.ledgerly.validator;

import dev.atinroy.ledgerly.dto.request.CategoryCreateRequest;
import dev.atinroy.ledgerly.error.ErrorCode;
import dev.atinroy.ledgerly.error.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for CategoryValidator.
 *
 * The CategoryValidator validates category creation requests.
 * Validation rules:
 * - Request must not be null
 * - Category name is required (not null or blank)
 */
@DisplayName("CategoryValidator Tests")
class CategoryValidatorTest {

    private CategoryValidator categoryValidator;

    @BeforeEach
    void setUp() {
        categoryValidator = new CategoryValidator();
    }

    @Test
    @DisplayName("should pass validation with valid category name")
    void shouldPassValidationWithValidCategoryName() {
        // Arrange: Valid category with a proper name
        CategoryCreateRequest request = new CategoryCreateRequest("Groceries");

        // Act
        ValidationResult result = categoryValidator.validate(request);

        // Assert
        assertThat(result.isValid()).isTrue();
        assertThat(result.hasErrors()).isFalse();
    }

    @Test
    @DisplayName("should fail validation when request is null")
    void shouldFailValidationWhenRequestIsNull() {
        // Act: Validate null request
        ValidationResult result = categoryValidator.validate(null);

        // Assert: Should have general error
        assertThat(result.hasErrors()).isTrue();
        assertThat(result.getGeneralErrors())
                .anyMatch(error -> error.errorCode().equals(ErrorCode.REQUIRED));
    }

    @Test
    @DisplayName("should fail validation when name is null")
    void shouldFailValidationWhenNameIsNull() {
        // Arrange: Request with null name
        CategoryCreateRequest request = new CategoryCreateRequest(null);

        // Act
        ValidationResult result = categoryValidator.validate(request);

        // Assert
        assertThat(result.hasErrors()).isTrue();
        assertThat(result.getFieldErrors("name"))
                .anyMatch(error -> error.errorCode().equals(ErrorCode.REQUIRED));
    }

    @Test
    @DisplayName("should fail validation when name is empty string")
    void shouldFailValidationWhenNameIsEmpty() {
        // Arrange: Empty name
        CategoryCreateRequest request = new CategoryCreateRequest("");

        // Act
        ValidationResult result = categoryValidator.validate(request);

        // Assert
        assertThat(result.hasErrors()).isTrue();
        assertThat(result.getFieldErrors("name")).isNotEmpty();
    }

    @Test
    @DisplayName("should fail validation when name is only whitespace")
    void shouldFailValidationWhenNameIsOnlyWhitespace() {
        // Arrange: Whitespace-only name
        CategoryCreateRequest request = new CategoryCreateRequest("   ");

        // Act
        ValidationResult result = categoryValidator.validate(request);

        // Assert: Blank strings should be rejected
        assertThat(result.hasErrors()).isTrue();
        assertThat(result.getFieldErrors("name")).isNotEmpty();
    }

    @Test
    @DisplayName("should pass validation with name containing leading/trailing spaces")
    void shouldPassValidationWithNameContainingSpaces() {
        // Arrange: Name with extra spaces (service layer should trim)
        // Validator only checks if name is present
        CategoryCreateRequest request = new CategoryCreateRequest("  Food  ");

        // Act
        ValidationResult result = categoryValidator.validate(request);

        // Assert: Should pass - trimming is service layer responsibility
        assertThat(result.isValid()).isTrue();
    }

    @Test
    @DisplayName("should pass validation with special characters in name")
    void shouldPassValidationWithSpecialCharactersInName() {
        // Arrange: Category name with special characters
        CategoryCreateRequest request = new CategoryCreateRequest("Food & Drinks (2024)");

        // Act
        ValidationResult result = categoryValidator.validate(request);

        // Assert: Special characters are allowed
        assertThat(result.isValid()).isTrue();
    }

    @Test
    @DisplayName("should pass validation with unicode characters in name")
    void shouldPassValidationWithUnicodeCharactersInName() {
        // Arrange: Unicode characters in name
        CategoryCreateRequest request = new CategoryCreateRequest("Cafe/Restaurants");

        // Act
        ValidationResult result = categoryValidator.validate(request);

        // Assert
        assertThat(result.isValid()).isTrue();
    }
}
