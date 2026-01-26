package dev.atinroy.ledgerly.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record BudgetUpdateRequest(
    @NotNull Long id,
    @PositiveOrZero BigDecimal amount,
    Long categoryId
) {}
