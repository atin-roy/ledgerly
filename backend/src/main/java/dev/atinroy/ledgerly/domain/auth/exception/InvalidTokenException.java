package dev.atinroy.ledgerly.domain.auth.exception;

import dev.atinroy.ledgerly.shared.error.LedgerlyException;
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
}
