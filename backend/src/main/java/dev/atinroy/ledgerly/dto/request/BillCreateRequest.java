package dev.atinroy.ledgerly.dto.request;

import dev.atinroy.ledgerly.entity.enums.BillStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BillCreateRequest(
    @NotBlank String name,
    @NotNull BigDecimal amount,
    @NotNull LocalDateTime dueDate,
    @NotNull BillStatus status
) {}
