package dev.atinroy.ledgerly.error;

public class TransactionNotFoundException extends LedgerlyException {
    public TransactionNotFoundException() {
        super("Transaction not found");
    }

    public TransactionNotFoundException(Long transactionId) {
        super("Could not find transaction with id: " + transactionId);
    }
}
