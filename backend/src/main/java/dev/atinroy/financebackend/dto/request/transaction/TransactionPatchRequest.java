package dev.atinroy.financebackend.dto.request.transaction;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionPatchRequest {
    private BigDecimal transactionAmount;
    private LocalDateTime transactionDate;
    private Long transactionTypeId;
    private String description;
    private String partyName;
    private Long budgetId;
}
