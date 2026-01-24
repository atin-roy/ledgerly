package dev.atinroy.financebackend.exception;

public class TransactionTypeNotFound extends RuntimeException {
    public TransactionTypeNotFound(String message) {
        super(message);
    }
}
