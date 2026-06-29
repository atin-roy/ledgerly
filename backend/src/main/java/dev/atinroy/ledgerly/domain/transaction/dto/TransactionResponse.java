package dev.atinroy.ledgerly.domain.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
    Long id,
    BigDecimal amount,
    LocalDateTime date,
    Long categoryId,
    String categoryName,
    String partyName,
    String description
) {}
