package dev.atinroy.ledgerly.error;

public class TransactionTypeNotFoundException extends LedgerlyException {
    public TransactionTypeNotFoundException(String message) {
        super(message);
    }
}
