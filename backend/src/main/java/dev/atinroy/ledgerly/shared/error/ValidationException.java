package dev.atinroy.ledgerly.shared.error;

public class ValidationException extends RuntimeException {
    private final ValidationResult validationResult;

    public ValidationException(ValidationResult validationResult) {
        super("Validation failed");
        this.validationResult = validationResult;
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }
}
