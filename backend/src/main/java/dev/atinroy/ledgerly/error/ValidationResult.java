package dev.atinroy.ledgerly.error;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public record ValidationResult(Set<ValidationError> errors) {

    public ValidationResult() {
        this(Collections.emptySet());
    }

    // -------- factory methods --------

    public static ValidationResult success() {
        return new ValidationResult(new LinkedHashSet<>());
    }

    public static ValidationResult withErrors() {
        return new ValidationResult(new LinkedHashSet<>());
    }

    // -------- adding errors --------

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

    // -------- querying --------

    public boolean isValid() {
        return errors.isEmpty();
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    @Override
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
