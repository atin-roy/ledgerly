package dev.atinroy.ledgerly.dto.request.transaction;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionCreateRequest {
    private String partyName; // needs to be named to create a new party on the fly
    private String description;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private LocalDateTime date;
    private Long budgetId; // optional
    @NotNull
    private Long typeId;
}
