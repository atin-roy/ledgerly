package dev.atinroy.ledgerly.error;

public class PotNotFoundException extends LedgerlyException {
    public PotNotFoundException(Long potId) {
        super("Could not find pot with id: " + potId);
    }
}
