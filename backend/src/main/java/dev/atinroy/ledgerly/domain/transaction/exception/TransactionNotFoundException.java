package dev.atinroy.ledgerly.domain.transaction.exception;

import dev.atinroy.ledgerly.shared.error.LedgerlyException;
public class TransactionNotFoundException extends LedgerlyException {
    public TransactionNotFoundException() {
        super("Transaction not found");
    }

    public TransactionNotFoundException(Long transactionId) {
        super("Could not find transaction with id: " + transactionId);
    }
}
