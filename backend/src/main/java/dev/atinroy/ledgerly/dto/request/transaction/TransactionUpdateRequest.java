package dev.atinroy.ledgerly.dto.request.transaction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionUpdateRequest(
    @NotBlank Long id,
    BigDecimal amount,
    LocalDateTime date,
    Long categoryId,
    String partyName // optional - create or link an existing party
) {}
