package dev.atinroy.ledgerly.exception;

public class BillNotFoundException extends LedgerlyException {
    public BillNotFoundException(Long billId) {
        super("Could not find bill with id: " + billId);
    }
}
