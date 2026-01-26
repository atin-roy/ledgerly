package dev.atinroy.ledgerly.validator;

import dev.atinroy.ledgerly.dto.request.transaction.TransactionCreateRequest;
import dev.atinroy.ledgerly.dto.request.transaction.TransactionUpdateRequest;
import dev.atinroy.ledgerly.error.ErrorCode;
import dev.atinroy.ledgerly.error.ValidationResult;
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

        if (request.id() == null) {
            result.addFieldError("id", ErrorCode.INVALID_VALUE, "Transaction ID is required");
        }

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

        if (date.isAfter(LocalDateTime.now())) {
            result.addFieldError("date", ErrorCode.INVALID_DATE,
                    "transaction date cannot be in the future");
        }
    }
}
