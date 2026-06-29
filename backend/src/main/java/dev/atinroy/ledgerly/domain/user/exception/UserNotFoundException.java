package dev.atinroy.ledgerly.domain.user.exception;

import dev.atinroy.ledgerly.shared.error.LedgerlyException;
public class UserNotFoundException extends LedgerlyException {
    public UserNotFoundException(String email) {
        super("Could not find user with email: " + email);
    }

    public UserNotFoundException(Long userId) {
        super("Could not find user with id: " + userId);
    }
}
