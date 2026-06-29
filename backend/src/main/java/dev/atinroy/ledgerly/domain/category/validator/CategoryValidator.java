package dev.atinroy.ledgerly.domain.category.validator;

import dev.atinroy.ledgerly.domain.category.dto.CategoryCreateRequest;
import dev.atinroy.ledgerly.shared.error.ErrorCode;
import dev.atinroy.ledgerly.shared.error.ValidationResult;
import org.springframework.stereotype.Component;

@Component
public class CategoryValidator {

    public ValidationResult validate(CategoryCreateRequest request) {
        ValidationResult result = ValidationResult.withErrors();

        if (request == null) {
            result.addGeneralError(
                    ErrorCode.REQUIRED,
                    "Category request must not be null"
            );
            return result;
        }

        validateName(request.name(), result);

        return result;
    }

    private void validateName(String name, ValidationResult result) {
        if (name == null || name.isBlank()) {
            result.addFieldError(
                    "name",
                    ErrorCode.REQUIRED,
                    "Category name is required"
            );
        }
    }
}
