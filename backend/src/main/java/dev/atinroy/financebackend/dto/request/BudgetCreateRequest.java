package dev.atinroy.financebackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BudgetCreateRequest {
    @NotBlank
    private String budgetName;
    @NotNull
    private BigDecimal budgetAmount;
    @NotNull
    private BigDecimal budgetSpent;
}
