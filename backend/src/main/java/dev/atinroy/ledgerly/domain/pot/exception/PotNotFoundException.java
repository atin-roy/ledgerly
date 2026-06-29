package dev.atinroy.ledgerly.domain.pot.exception;

import dev.atinroy.ledgerly.shared.error.LedgerlyException;
public class PotNotFoundException extends LedgerlyException {
    public PotNotFoundException(Long potId) {
        super("Could not find pot with id: " + potId);
    }
}
