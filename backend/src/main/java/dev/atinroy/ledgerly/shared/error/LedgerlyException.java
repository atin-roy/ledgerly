package dev.atinroy.ledgerly.shared.error;

/**
 * Base error class for all Ledgerly application exceptions.
 * All domain-specific exceptions should extend this class for consistent error handling.
 */
public class LedgerlyException extends RuntimeException {
    public LedgerlyException(String message) {
        super(message);
    }

    public LedgerlyException(String message, Throwable cause) {
        super(message, cause);
    }
}
