package dev.atinroy.ledgerly.domain.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionUpdateRequest(
    BigDecimal amount,
    LocalDateTime date,
    Long categoryId,
    String partyName,
    String description
) {}
