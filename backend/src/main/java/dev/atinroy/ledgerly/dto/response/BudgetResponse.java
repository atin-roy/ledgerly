package dev.atinroy.ledgerly.dto.response;
import java.math.BigDecimal;


public record BudgetResponse (
    Long id,
    BigDecimal amount,
    Long categoryId
) {}
