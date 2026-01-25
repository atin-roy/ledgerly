package dev.atinroy.ledgerly.dto.request.transaction;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionPatchRequest {
    private BigDecimal amount;
    private LocalDateTime date;
    private Long typeId;
    private String description;
    private String partyName;
    private Long budgetId;
}
