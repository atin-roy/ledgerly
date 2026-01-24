package dev.atinroy.financebackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionCreateRequest {
    @NotBlank
    private String partyName;
    private String description;
    @NotNull
    private BigDecimal transactionAmount;
    @NotNull
    private LocalDateTime transactionDate;
    private Long budgetId; // optional
    @NotNull
    private Long transactionTypeId;
    @NotNull
    private Boolean isSystemType; // true for SystemTransactionType, false for UserTransactionType
}
