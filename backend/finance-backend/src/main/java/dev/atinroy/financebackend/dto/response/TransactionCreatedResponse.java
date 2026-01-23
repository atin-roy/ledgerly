package dev.atinroy.financebackend.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionCreatedResponse {
    private String id;
    private BigDecimal amount;
    private LocalDateTime createdAt;
}
