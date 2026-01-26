package dev.atinroy.ledgerly.error;

/**
 * Central repository for all error codes used throughout the Ledgerly application.
 * These error codes are used to categorize validation errors and business logic violations.
 */
public final class ErrorCode {
    // Resource Not Found Errors
    public static final String USER_NOT_FOUND = "User not found";
    public static final String BILL_NOT_FOUND = "Bill not found";
    public static final String BUDGET_NOT_FOUND = "Budget not found";
    public static final String CATEGORY_NOT_FOUND = "Category not found";
    public static final String PARTY_NOT_FOUND = "Party not found";
    public static final String POT_NOT_FOUND = "Pot not found";
    public static final String TRANSACTION_NOT_FOUND = "Transaction not found";
    public static final String TRANSACTION_TYPE_NOT_FOUND = "Transaction type not found";

    // Common Validation Errors
    public static final String REQUIRED = "This field is required";
    public static final String INVALID_FORMAT = "The provided format is invalid";
    public static final String ALREADY_EXISTS = "This value already exists";
    public static final String TOO_SHORT = "The value is too short";
    public static final String TOO_LONG = "The value is too long";
    public static final String INVALID_VALUE = "The value does not meet business requirements";

    // Field-Specific Validation Errors
    public static final String INVALID_EMAIL = "The email format is invalid";
    public static final String INVALID_USERNAME = "The username format is invalid";
    public static final String EMAIL_ALREADY_EXISTS = "An account with this email already exists";
    public static final String USERNAME_ALREADY_EXISTS = "This username is already taken";

    // Business Logic Errors
    public static final String NEGATIVE_AMOUNT = "Amount cannot be negative";
    public static final String ZERO_AMOUNT = "Amount must be greater than zero";
    public static final String INVALID_DATE = "The date is invalid or in the past";
    public static final String INVALID_DATE_RANGE = "The end date must be after the start date";

    private ErrorCode() {}
}
