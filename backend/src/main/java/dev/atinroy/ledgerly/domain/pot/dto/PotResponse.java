package dev.atinroy.ledgerly.domain.pot.dto;

import java.math.BigDecimal;

public record PotResponse(
    Long id,
    String name,
    BigDecimal target,
    BigDecimal saved
) {}
