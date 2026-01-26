package dev.atinroy.ledgerly.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
    Long id,
    BigDecimal amount,
    LocalDateTime date,
    String categoryName,
    String partyName
) {}
