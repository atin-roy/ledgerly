package dev.atinroy.ledgerly.domain.transaction.exception;

import dev.atinroy.ledgerly.shared.error.LedgerlyException;
public class TransactionTypeNotFoundException extends LedgerlyException {
    public TransactionTypeNotFoundException(String message) {
        super(message);
    }
}
