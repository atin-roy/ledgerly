package dev.atinroy.financebackend.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PotCreateResponse {
    private String id;
    private String potName;
    private BigDecimal amount;
    private BigDecimal saved;
    private LocalDateTime createdDate;
}
