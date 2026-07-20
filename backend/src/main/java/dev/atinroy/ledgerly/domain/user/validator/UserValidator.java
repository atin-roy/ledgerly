package dev.atinroy.ledgerly.domain.user.validator;

import dev.atinroy.ledgerly.domain.user.dto.UserCreateRequest;
import dev.atinroy.ledgerly.domain.user.dto.UserUpdateRequest;
import dev.atinroy.ledgerly.shared.error.ErrorCode;
import dev.atinroy.ledgerly.shared.error.ValidationResult;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {

    public ValidationResult validate(UserCreateRequest request) {
        ValidationResult result = ValidationResult.withErrors();

        if (request == null) {
            result.addGeneralError(
                    ErrorCode.REQUIRED,
                    "User request must not be null"
            );
            return result;
        }

        validateEmail(request.email(), result);
        validateUsername(request.username(), result);
        validatePassword(request.password(), result);

        return result;
    }

    public ValidationResult validate(UserUpdateRequest request) {
        ValidationResult result = ValidationResult.withErrors();

        if (request == null) {
            result.addGeneralError(
                    ErrorCode.REQUIRED,
                    "User request must not be null"
            );
            return result;
        }

        if (request.email() != null) {
            validateEmail(request.email(), result);
        }

        if (request.username() != null) {
            validateUsername(request.username(), result);
        }

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

    /** Standalone password-strength check for the change-password flow. */
    public ValidationResult validateNewPassword(String password) {
        ValidationResult result = ValidationResult.withErrors();
        validatePassword(password, result);
        return result;
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
