package dev.atinroy.financebackend.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String email) {
        super("Could not find user with email: " + email);
    }
    public UserNotFoundException(Long userId) {
        super("Could not find user with id: " + userId);
    }
}
