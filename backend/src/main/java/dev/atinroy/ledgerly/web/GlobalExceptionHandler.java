package dev.atinroy.ledgerly.web;

import dev.atinroy.ledgerly.domain.auth.exception.InvalidCredentialsException;
import dev.atinroy.ledgerly.domain.auth.exception.InvalidTokenException;
import dev.atinroy.ledgerly.domain.bill.exception.BillNotFoundException;
import dev.atinroy.ledgerly.domain.budget.exception.BudgetNotFoundException;
import dev.atinroy.ledgerly.domain.category.exception.CategoryNotFoundException;
import dev.atinroy.ledgerly.domain.party.exception.PartyNotFoundException;
import dev.atinroy.ledgerly.domain.pot.exception.PotNotFoundException;
import dev.atinroy.ledgerly.domain.transaction.exception.TransactionNotFoundException;
import dev.atinroy.ledgerly.domain.user.exception.UserNotFoundException;
import dev.atinroy.ledgerly.shared.error.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Validation failed");
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> fieldErrors.put(err.getField(), err.getDefaultMessage()));
        response.put("details", fieldErrors);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(ValidationException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Validation failed");
        response.put("details", ex.getValidationResult().errors());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFoundException(UserNotFoundException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "User not found");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCategoryNotFoundException(CategoryNotFoundException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Category not found");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleTransactionNotFoundException(TransactionNotFoundException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Transaction not found");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(BudgetNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleBudgetNotFoundException(BudgetNotFoundException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Budget not found");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(PotNotFoundException.class)
    public ResponseEntity<Map<String, String>> handlePotNotFoundException(PotNotFoundException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Pot not found");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(PartyNotFoundException.class)
    public ResponseEntity<Map<String, String>> handlePartyNotFoundException(PartyNotFoundException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Party not found");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(BillNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleBillNotFoundException(BillNotFoundException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Bill not found");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Authentication failed");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Map<String, String>> handleInvalidTokenException(InvalidTokenException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Invalid token");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDeniedException(org.springframework.security.access.AccessDeniedException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Access denied");
        response.put("message", "You do not have permission to access this resource");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        // Log the full exception for debugging
        log.error("Unhandled exception occurred", ex);
        
        Map<String, String> response = new HashMap<>();
        response.put("error", "Internal server error");
        
        // Only expose detailed error messages in non-production environments
        if ("prod".equalsIgnoreCase(activeProfile)) {
            response.put("message", "An unexpected error occurred. Please contact support if the problem persists.");
        } else {
            response.put("message", ex.getMessage());
            response.put("type", ex.getClass().getSimpleName());
        }
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
