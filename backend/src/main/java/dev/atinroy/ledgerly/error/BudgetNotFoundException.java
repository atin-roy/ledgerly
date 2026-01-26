package dev.atinroy.ledgerly.error;

public class BudgetNotFoundException extends LedgerlyException {
    public BudgetNotFoundException(Long budgetId) {
        super("Could not find budget with id: " + budgetId);
    }
}
