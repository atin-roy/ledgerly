package dev.atinroy.ledgerly.exception;

public class TransactionTypeNotFoundException extends LedgerlyException {
    public TransactionTypeNotFoundException(String message) {
        super(message);
    }
}
