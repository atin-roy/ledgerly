package dev.atinroy.ledgerly.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BudgetResponse {
    private Long budgetId;
    private BigDecimal amount;
    private Long categoryId;
}
