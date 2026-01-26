package dev.atinroy.ledgerly.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PotResponse {
    private Long potId;
    private String name;
    private BigDecimal target;
    private BigDecimal saved;
}
