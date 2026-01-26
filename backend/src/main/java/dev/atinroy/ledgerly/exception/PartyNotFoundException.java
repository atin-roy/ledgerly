package dev.atinroy.ledgerly.exception;

public class PartyNotFoundException extends LedgerlyException {
    public PartyNotFoundException(Long partyId) {
        super("Could not find party with id: " + partyId);
    }
}
