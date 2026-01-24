package dev.atinroy.financebackend.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionCreatedResponse {
    private String transactionId;
    private BigDecimal transactionAmount;
    private LocalDateTime createdAt;
}
