package dev.atinroy.ledgerly.domain.budget.dto;

import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record BudgetUpdateRequest(
    Long id,
    @PositiveOrZero BigDecimal amount,
    Long categoryId
) {}
