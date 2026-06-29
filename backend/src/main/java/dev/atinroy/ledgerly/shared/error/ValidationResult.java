package dev.atinroy.ledgerly.shared.error;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidationResult {

    private final Set<ValidationError> errors = new LinkedHashSet<>();

    public static ValidationResult success() {
        return new ValidationResult();
    }

    public static ValidationResult withErrors() {
        return new ValidationResult();
    }

    public void addError(String fieldName, String errorCode, String message) {
        errors.add(new ValidationError(fieldName, errorCode, message));
    }

    public void addFieldError(String fieldName, String errorCode, String message) {
        addError(fieldName, errorCode, message);
    }

    public void addGeneralError(String errorCode, String message) {
        addError(null, errorCode, message);
    }

    public void merge(ValidationResult other) {
        this.errors.addAll(other.errors);
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public Set<ValidationError> errors() {
        return Collections.unmodifiableSet(errors);
    }

    public Set<ValidationError> getFieldErrors(String fieldName) {
        return errors.stream()
                .filter(e -> fieldName.equals(e.fieldName()))
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<ValidationError> getGeneralErrors() {
        return errors.stream()
                .filter(e -> e.fieldName() == null)
                .collect(Collectors.toUnmodifiableSet());
    }
}
