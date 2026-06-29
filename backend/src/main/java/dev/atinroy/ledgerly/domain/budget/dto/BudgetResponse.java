package dev.atinroy.ledgerly.domain.budget.dto;
import java.math.BigDecimal;


public record BudgetResponse (
    Long id,
    BigDecimal amount,
    Long categoryId,
    BigDecimal spent
) {}
