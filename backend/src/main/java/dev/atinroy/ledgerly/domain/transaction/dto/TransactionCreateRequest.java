package dev.atinroy.ledgerly.domain.transaction.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionCreateRequest(
    @NotNull BigDecimal amount,
    @NotNull LocalDateTime date,
    @NotNull Long categoryId,
    String partyName, // optional - create or link an existing party
    String description // optional - transaction notes
) {}
