package dev.atinroy.ledgerly.domain.bill.exception;

import dev.atinroy.ledgerly.shared.error.LedgerlyException;
public class BillNotFoundException extends LedgerlyException {
    public BillNotFoundException(Long billId) {
        super("Could not find bill with id: " + billId);
    }
}
