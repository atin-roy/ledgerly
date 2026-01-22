package dev.atinroy.financebackend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionCreatedResponse {
    private String id;
    private BigDecimal amount;
    private LocalDateTime createdAt;
}
