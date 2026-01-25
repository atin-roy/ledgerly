package dev.atinroy.ledgerly.exception;

public class BudgetNotFoundException extends RuntimeException {
    public BudgetNotFoundException(Long budgetId) {
        super("Could not find budget with id: " + budgetId);
    }
}
