package dev.atinroy.ledgerly.domain.auth.exception;

import dev.atinroy.ledgerly.shared.error.LedgerlyException;
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
