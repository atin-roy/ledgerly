package dev.atinroy.ledgerly.domain.budget.exception;

import dev.atinroy.ledgerly.shared.error.LedgerlyException;
public class BudgetNotFoundException extends LedgerlyException {
    public BudgetNotFoundException(Long budgetId) {
        super("Could not find budget with id: " + budgetId);
    }
}
