package dev.atinroy.ledgerly.domain.pot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record PotCreateRequest(
    @NotBlank String name,
    @NotNull @PositiveOrZero BigDecimal target,
    @NotNull BigDecimal saved
) {}
