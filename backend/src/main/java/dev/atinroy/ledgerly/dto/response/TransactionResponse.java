package dev.atinroy.ledgerly.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionResponse {
    private Long id;
    private BigDecimal amount;
    private LocalDateTime date;
    private String categoryName;
    private String partyName;
}
