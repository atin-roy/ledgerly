package dev.atinroy.ledgerly.error;

import java.util.Objects;

public record ValidationError(
        String fieldName,
        String errorCode,
        String message) {
    public ValidationError {
        Objects.requireNonNull(errorCode, "errorCode must not be null");
        Objects.requireNonNull(message, "message must not be null");
    }
}
