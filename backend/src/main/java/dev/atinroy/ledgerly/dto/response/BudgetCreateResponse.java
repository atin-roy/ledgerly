package dev.atinroy.ledgerly.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BudgetCreateResponse {
    private String budgetId;
    private String name;
    private BigDecimal amount;
    private BigDecimal spent;
}
