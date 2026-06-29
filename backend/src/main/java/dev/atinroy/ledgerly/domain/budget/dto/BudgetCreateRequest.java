package dev.atinroy.ledgerly.domain.budget.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record BudgetCreateRequest(
    @NotNull @PositiveOrZero BigDecimal amount,
    @NotNull Long categoryId
) {}
