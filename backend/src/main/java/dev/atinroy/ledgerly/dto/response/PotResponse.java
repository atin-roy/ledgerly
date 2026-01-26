package dev.atinroy.ledgerly.dto.response;

import java.math.BigDecimal;

public record PotResponse(
    Long id,
    String name,
    BigDecimal target,
    BigDecimal saved
) {}
