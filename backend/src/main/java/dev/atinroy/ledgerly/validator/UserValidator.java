package dev.atinroy.ledgerly.validator;

import dev.atinroy.ledgerly.entity.User;
import dev.atinroy.ledgerly.error.ErrorCode;
import dev.atinroy.ledgerly.error.ValidationResult;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {

    public ValidationResult validate(User user) {
        ValidationResult result = ValidationResult.withErrors();

        if (user == null) {
            result.addGeneralError(
                    ErrorCode.REQUIRED,
                    "User must not be null"
            );
            return result;
        }

        validateEmail(user.getEmail(), result);
        validateUsername(user.getUsername(), result);
        validatePassword(user.getPassword(), result);

        return result;
    }

    private void validateEmail(String email, ValidationResult result) {
        if (email == null || email.isBlank()) {
            result.addFieldError(
                    "email",
                    ErrorCode.REQUIRED,
                    "Email is required"
            );
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            result.addFieldError(
                    "email",
                    ErrorCode.INVALID_FORMAT,
                    "Email format is invalid"
            );
        }
    }

    private void validateUsername(String username, ValidationResult result) {
        if (username == null || username.isBlank()) {
            result.addFieldError(
                    "username",
                    ErrorCode.REQUIRED,
                    "Username is required"
            );
        }
    }

    private void validatePassword(String password, ValidationResult result) {
        if (password == null || password.length() < 8) {
            result.addFieldError(
                    "password",
                    ErrorCode.TOO_SHORT,
                    "Password must be at least 8 characters"
            );
        }
    }
}
