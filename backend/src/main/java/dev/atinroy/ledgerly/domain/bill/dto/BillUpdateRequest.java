package dev.atinroy.ledgerly.domain.bill.dto;

import dev.atinroy.ledgerly.domain.bill.entity.BillStatus;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BillUpdateRequest(
    Long id,
    String name,
    @PositiveOrZero BigDecimal amount,
    LocalDateTime dueDate,
    BillStatus status
) {}
