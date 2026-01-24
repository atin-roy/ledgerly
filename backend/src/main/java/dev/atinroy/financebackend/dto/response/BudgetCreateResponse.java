package dev.atinroy.financebackend.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BudgetCreateResponse {
    private String id;
    private String budgetName;
    private String budgetStatus;
    private BigDecimal budgetAmount;
    private BigDecimal budgetSpent;
}
