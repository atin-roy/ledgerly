package dev.atinroy.ledgerly.exception;

public class PotNotFoundException extends LedgerlyException {
    public PotNotFoundException(Long potId) {
        super("Could not find pot with id: " + potId);
    }
}
