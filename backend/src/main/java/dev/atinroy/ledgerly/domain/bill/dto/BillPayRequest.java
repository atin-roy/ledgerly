package dev.atinroy.ledgerly.domain.bill.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record BillPayRequest(
    @NotNull Long categoryId,
    @NotNull LocalDateTime date // client-local, so the entry lands on the user's day, not the server's
) {}
