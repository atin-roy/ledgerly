package dev.atinroy.ledgerly.domain.bill.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BillResponse(
    Long id,
    String name,
    BigDecimal amount,
    String status,
    LocalDateTime dueDate
) {}
