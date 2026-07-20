package dev.atinroy.ledgerly.domain.transaction.validator;

import dev.atinroy.ledgerly.domain.transaction.dto.TransactionCreateRequest;
import dev.atinroy.ledgerly.domain.transaction.dto.TransactionUpdateRequest;
import dev.atinroy.ledgerly.shared.error.ErrorCode;
import dev.atinroy.ledgerly.shared.error.ValidationResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class TransactionValidator {

    public ValidationResult validate(TransactionCreateRequest request) {
        ValidationResult result = ValidationResult.withErrors();

        validateDate(request.date(), result);
        validateAmount(request.amount(), result);
        validateCategoryId(request.categoryId(), result);

        return result;
    }

    public ValidationResult validate(TransactionUpdateRequest request) {
        ValidationResult result = ValidationResult.withErrors();

        if (request.date() != null) {
            validateDate(request.date(), result);
        }
        if (request.amount() != null) {
            validateAmount(request.amount(), result);
        }
        if (request.categoryId() != null) {
            validateCategoryId(request.categoryId(), result);
        }

        return result;
    }

    private void validateCategoryId(Long categoryId, ValidationResult result) {
        if (categoryId == null) {
            result.addFieldError("categoryId", ErrorCode.INVALID_VALUE, "Category ID is required");
        } else if (categoryId < 0) {
            result.addFieldError("categoryId", ErrorCode.INVALID_VALUE, "Category ID must be 0 or greater");
        }
    }

    private void validateAmount(BigDecimal amount, ValidationResult result) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
            result.addFieldError("amount", ErrorCode.INVALID_VALUE, "Amount cannot be zero");
        }
    }

    private void validateDate(LocalDateTime date, ValidationResult result) {
        if (date == null) {
            result.addFieldError("date", ErrorCode.INVALID_DATE, "Date is required");
            return;
        }

        // allow through end of tomorrow: clients send local wall-clock dates and
        // the server runs in UTC, so "today" for a user can be "tomorrow" here
        if (date.toLocalDate().isAfter(java.time.LocalDate.now().plusDays(1))) {
            result.addFieldError("date", ErrorCode.INVALID_DATE,
                    "transaction date cannot be in the future");
        }
    }
}
