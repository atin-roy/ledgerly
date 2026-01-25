package dev.atinroy.ledgerly.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PotCreateResponse {
    private String potId;
    private String name;
    private BigDecimal target;
    private BigDecimal saved;
    private LocalDateTime createdAt;
}
