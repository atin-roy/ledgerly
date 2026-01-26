package dev.atinroy.ledgerly.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class BillResponse {
    private Long billId;
    private String name;
    private BigDecimal amount;
    private String status;
    private LocalDateTime dueDate;
}
