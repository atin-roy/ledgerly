package dev.atinroy.ledgerly.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PotResponse {
    private Long id;
    private String name;
    private BigDecimal target;
    private BigDecimal saved;
}
