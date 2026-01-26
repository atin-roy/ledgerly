package dev.atinroy.ledgerly.dto.request.transaction;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionUpdateRequest {
    private BigDecimal amount;
    private LocalDateTime date;
    private Long categoryId;
    private String partyName; // optional - create or link an existing party
}
