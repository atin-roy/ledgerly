package dev.atinroy.ledgerly.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionCreatedResponse {
    private Long transactionId;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private String typeName;
}
