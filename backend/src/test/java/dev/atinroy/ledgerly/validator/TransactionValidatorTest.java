package dev.atinroy.ledgerly.validator;

import dev.atinroy.ledgerly.dto.request.transaction.TransactionCreateRequest;
import dev.atinroy.ledgerly.dto.request.transaction.TransactionUpdateRequest;
import dev.atinroy.ledgerly.error.ErrorCode;
import dev.atinroy.ledgerly.error.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for TransactionValidator.
 *
 * The TransactionValidator validates transaction requests with these rules:
 * - Date: Required and cannot be in the future
 * - Amount: Required and cannot be zero (positive = income, negative = expense)
 * - CategoryId: Required and must be >= 0 (0 maps to "General" category)
 */
@DisplayName("TransactionValidator Tests")
class TransactionValidatorTest {

    private TransactionValidator transactionValidator;

    @BeforeEach
    void setUp() {
        transactionValidator = new TransactionValidator();
    }

    // ==================== TransactionCreateRequest Validation Tests ====================

    @Nested
    @DisplayName("TransactionCreateRequest Validation")
    class TransactionCreateRequestValidation {

        @Test
        @DisplayName("should pass validation with valid transaction data")
        void shouldPassValidationWithValidTransactionData() {
            // Arrange: Create a valid transaction request
            // - Past date (not in future)
            // - Non-zero amount (positive for income)
            // - Valid category ID
            TransactionCreateRequest request = new TransactionCreateRequest(
                    new BigDecimal("100.50"),           // Positive amount (income)
                    LocalDateTime.now().minusDays(1),   // Yesterday (not in future)
                    1L,                                  // Valid category ID
                    "Coffee Shop"                        // Optional party name
            );

            // Act
            ValidationResult result = transactionValidator.validate(request);

            // Assert
            assertThat(result.isValid()).isTrue();
            assertThat(result.hasErrors()).isFalse();
        }

        @Test
        @DisplayName("should pass validation with negative amount (expense)")
        void shouldPassValidationWithNegativeAmount() {
            // Arrange: Negative amount represents an expense
            TransactionCreateRequest request = new TransactionCreateRequest(
                    new BigDecimal("-50.00"),           // Negative = expense
                    LocalDateTime.now().minusHours(1),
                    1L,
                    null
            );

            // Act
            ValidationResult result = transactionValidator.validate(request);

            // Assert: Negative amounts are valid (represents spending)
            assertThat(result.isValid()).isTrue();
        }

        @Test
        @DisplayName("should pass validation with category ID 0 (General category)")
        void shouldPassValidationWithCategoryIdZero() {
            // Arrange: Category ID 0 is special - maps to "General" category
            TransactionCreateRequest request = new TransactionCreateRequest(
                    new BigDecimal("25.00"),
                    LocalDateTime.now().minusMinutes(30),
                    0L,  // Zero maps to General category
                    null
            );

            // Act
            ValidationResult result = transactionValidator.validate(request);

            // Assert: Category ID 0 should be valid
            assertThat(result.isValid()).isTrue();
        }

        @Test
        @DisplayName("should fail validation when date is null")
        void shouldFailValidationWhenDateIsNull() {
            // Arrange: Missing date
            TransactionCreateRequest request = new TransactionCreateRequest(
                    new BigDecimal("100.00"),
                    null,  // Missing date
                    1L,
                    null
            );

            // Act
            ValidationResult result = transactionValidator.validate(request);

            // Assert
            assertThat(result.hasErrors()).isTrue();
            assertThat(result.getFieldErrors("date"))
                    .anyMatch(error -> error.errorCode().equals(ErrorCode.INVALID_DATE));
        }

        @Test
        @DisplayName("should fail validation when date is in the future")
        void shouldFailValidationWhenDateIsInFuture() {
            // Arrange: Future date - not allowed for transactions
            TransactionCreateRequest request = new TransactionCreateRequest(
                    new BigDecimal("100.00"),
                    LocalDateTime.now().plusDays(1),  // Tomorrow - invalid
                    1L,
                    null
            );

            // Act
            ValidationResult result = transactionValidator.validate(request);

            // Assert: Future dates should be rejected
            assertThat(result.hasErrors()).isTrue();
            assertThat(result.getFieldErrors("date"))
                    .anyMatch(error -> error.errorCode().equals(ErrorCode.INVALID_DATE));
        }

        @Test
        @DisplayName("should fail validation when amount is null")
        void shouldFailValidationWhenAmountIsNull() {
            // Arrange: Missing amount
            TransactionCreateRequest request = new TransactionCreateRequest(
                    null,  // Missing amount
                    LocalDateTime.now().minusHours(1),
                    1L,
                    null
            );

            // Act
            ValidationResult result = transactionValidator.validate(request);

            // Assert
            assertThat(result.hasErrors()).isTrue();
            assertThat(result.getFieldErrors("amount"))
                    .anyMatch(error -> error.errorCode().equals(ErrorCode.INVALID_VALUE));
        }

        @Test
        @DisplayName("should fail validation when amount is zero")
        void shouldFailValidationWhenAmountIsZero() {
            // Arrange: Zero amount - doesn't make sense for a transaction
            TransactionCreateRequest request = new TransactionCreateRequest(
                    BigDecimal.ZERO,  // Zero is invalid
                    LocalDateTime.now().minusHours(1),
                    1L,
                    null
            );

            // Act
            ValidationResult result = transactionValidator.validate(request);

            // Assert: Zero amounts should be rejected
            assertThat(result.hasErrors()).isTrue();
            assertThat(result.getFieldErrors("amount"))
                    .anyMatch(error -> error.errorCode().equals(ErrorCode.INVALID_VALUE));
        }

        @Test
        @DisplayName("should fail validation when category ID is null")
        void shouldFailValidationWhenCategoryIdIsNull() {
            // Arrange: Missing category ID
            TransactionCreateRequest request = new TransactionCreateRequest(
                    new BigDecimal("100.00"),
                    LocalDateTime.now().minusHours(1),
                    null,  // Missing category ID
                    null
            );

            // Act
            ValidationResult result = transactionValidator.validate(request);

            // Assert
            assertThat(result.hasErrors()).isTrue();
            assertThat(result.getFieldErrors("categoryId"))
                    .anyMatch(error -> error.errorCode().equals(ErrorCode.INVALID_VALUE));
        }

        @Test
        @DisplayName("should fail validation when category ID is negative")
        void shouldFailValidationWhenCategoryIdIsNegative() {
            // Arrange: Negative category ID - invalid
            TransactionCreateRequest request = new TransactionCreateRequest(
                    new BigDecimal("100.00"),
                    LocalDateTime.now().minusHours(1),
                    -1L,  // Negative - invalid
                    null
            );

            // Act
            ValidationResult result = transactionValidator.validate(request);

            // Assert
            assertThat(result.hasErrors()).isTrue();
            assertThat(result.getFieldErrors("categoryId"))
                    .anyMatch(error -> error.errorCode().equals(ErrorCode.INVALID_VALUE));
        }

        @Test
        @DisplayName("should pass validation without party name (optional)")
        void shouldPassValidationWithoutPartyName() {
            // Arrange: Party name is optional
            TransactionCreateRequest request = new TransactionCreateRequest(
                    new BigDecimal("100.00"),
                    LocalDateTime.now().minusHours(1),
                    1L,
                    null  // No party - that's okay
            );

            // Act
            ValidationResult result = transactionValidator.validate(request);

            // Assert
            assertThat(result.isValid()).isTrue();
        }

        @Test
        @DisplayName("should collect multiple validation errors")
        void shouldCollectMultipleValidationErrors() {
            // Arrange: Multiple invalid fields
            TransactionCreateRequest request = new TransactionCreateRequest(
                    BigDecimal.ZERO,                      // Invalid: zero amount
                    LocalDateTime.now().plusDays(1),      // Invalid: future date
                    -1L,                                   // Invalid: negative category
                    null
            );

            // Act
            ValidationResult result = transactionValidator.validate(request);

            // Assert: Should have errors for all three fields
            assertThat(result.hasErrors()).isTrue();
            assertThat(result.getFieldErrors("amount")).isNotEmpty();
            assertThat(result.getFieldErrors("date")).isNotEmpty();
            assertThat(result.getFieldErrors("categoryId")).isNotEmpty();
        }
    }

    // ==================== TransactionUpdateRequest Validation Tests ====================

    @Nested
    @DisplayName("TransactionUpdateRequest Validation")
    class TransactionUpdateRequestValidation {

        @Test
        @DisplayName("should fail validation when transaction ID is null")
        void shouldFailValidationWhenIdIsNull() {
            // Arrange: Update request requires transaction ID
            TransactionUpdateRequest request = new TransactionUpdateRequest(
                    null,  // Missing ID
                    new BigDecimal("100.00"),
                    LocalDateTime.now().minusHours(1),
                    1L,
                    null
            );

            // Act
            ValidationResult result = transactionValidator.validate(request);

            // Assert
            assertThat(result.hasErrors()).isTrue();
            assertThat(result.getFieldErrors("id"))
                    .anyMatch(error -> error.errorCode().equals(ErrorCode.INVALID_VALUE));
        }

        @Test
        @DisplayName("should pass validation with only ID (no updates)")
        void shouldPassValidationWithOnlyId() {
            // Arrange: Only ID provided - no fields to update (edge case)
            TransactionUpdateRequest request = new TransactionUpdateRequest(
                    1L,    // Transaction ID
                    null,  // Not updating amount
                    null,  // Not updating date
                    null,  // Not updating category
                    null   // Not updating party
            );

            // Act
            ValidationResult result = transactionValidator.validate(request);

            // Assert: Should be valid - only ID is required
            assertThat(result.isValid()).isTrue();
        }

        @Test
        @DisplayName("should validate provided date (not in future)")
        void shouldValidateProvidedDate() {
            // Arrange: Updating date with future date
            TransactionUpdateRequest request = new TransactionUpdateRequest(
                    1L,
                    null,
                    LocalDateTime.now().plusDays(1),  // Future date - invalid
                    null,
                    null
            );

            // Act
            ValidationResult result = transactionValidator.validate(request);

            // Assert
            assertThat(result.hasErrors()).isTrue();
            assertThat(result.getFieldErrors("date")).isNotEmpty();
        }

        @Test
        @DisplayName("should validate provided amount (not zero)")
        void shouldValidateProvidedAmount() {
            // Arrange: Updating amount with zero
            TransactionUpdateRequest request = new TransactionUpdateRequest(
                    1L,
                    BigDecimal.ZERO,  // Zero - invalid
                    null,
                    null,
                    null
            );

            // Act
            ValidationResult result = transactionValidator.validate(request);

            // Assert
            assertThat(result.hasErrors()).isTrue();
            assertThat(result.getFieldErrors("amount")).isNotEmpty();
        }

        @Test
        @DisplayName("should validate provided category ID (not negative)")
        void shouldValidateProvidedCategoryId() {
            // Arrange: Updating category with negative ID
            TransactionUpdateRequest request = new TransactionUpdateRequest(
                    1L,
                    null,
                    null,
                    -5L,  // Negative - invalid
                    null
            );

            // Act
            ValidationResult result = transactionValidator.validate(request);

            // Assert
            assertThat(result.hasErrors()).isTrue();
            assertThat(result.getFieldErrors("categoryId")).isNotEmpty();
        }

        @Test
        @DisplayName("should pass validation with valid partial update")
        void shouldPassValidationWithValidPartialUpdate() {
            // Arrange: Valid partial update - only updating amount
            TransactionUpdateRequest request = new TransactionUpdateRequest(
                    1L,
                    new BigDecimal("200.00"),  // New amount
                    null,                       // Keep existing date
                    null,                       // Keep existing category
                    null                        // Keep existing party
            );

            // Act
            ValidationResult result = transactionValidator.validate(request);

            // Assert
            assertThat(result.isValid()).isTrue();
        }

        @Test
        @DisplayName("should pass validation with all valid updates")
        void shouldPassValidationWithAllValidUpdates() {
            // Arrange: Valid full update
            TransactionUpdateRequest request = new TransactionUpdateRequest(
                    1L,
                    new BigDecimal("-75.50"),             // New expense amount
                    LocalDateTime.now().minusHours(2),   // New date
                    2L,                                    // New category
                    "New Party"                            // New party
            );

            // Act
            ValidationResult result = transactionValidator.validate(request);

            // Assert
            assertThat(result.isValid()).isTrue();
        }
    }
}
