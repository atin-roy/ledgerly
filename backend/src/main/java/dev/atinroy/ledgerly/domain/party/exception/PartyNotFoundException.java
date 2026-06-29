package dev.atinroy.ledgerly.domain.party.exception;

import dev.atinroy.ledgerly.shared.error.LedgerlyException;
public class PartyNotFoundException extends LedgerlyException {
    public PartyNotFoundException(Long partyId) {
        super("Could not find party with id: " + partyId);
    }
}
