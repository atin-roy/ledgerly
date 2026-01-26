package dev.atinroy.ledgerly.error;

public class BillNotFoundException extends LedgerlyException {
    public BillNotFoundException(Long billId) {
        super("Could not find bill with id: " + billId);
    }
}
