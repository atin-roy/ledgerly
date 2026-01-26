package dev.atinroy.ledgerly.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record PotUpdateRequest(
    @NotNull Long id,
    String name,
    @PositiveOrZero BigDecimal target,
    BigDecimal saved
) {}
