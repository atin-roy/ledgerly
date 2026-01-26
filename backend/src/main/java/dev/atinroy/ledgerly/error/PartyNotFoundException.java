package dev.atinroy.ledgerly.error;

public class PartyNotFoundException extends LedgerlyException {
    public PartyNotFoundException(Long partyId) {
        super("Could not find party with id: " + partyId);
    }
}
