package dev.atinroy.financebackend.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BudgetCreateResponse {
    private String budgetId;
    private String budgetName;
    private BigDecimal budgetAmount;
    private BigDecimal budgetSpent;
}
