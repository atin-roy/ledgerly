package dev.atinroy.ledgerly.controller;

import dev.atinroy.ledgerly.error.ValidationException;
import dev.atinroy.ledgerly.error.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

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
        Map<String, String> response = new HashMap<>();
        response.put("error", "Internal server error");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
