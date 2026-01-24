package dev.atinroy.financebackend.exception;

public class BudgetNotFoundException extends RuntimeException {
    public BudgetNotFoundException(Long budgetId) {
        super("Could not find budget with id: " + budgetId);
    }
}
