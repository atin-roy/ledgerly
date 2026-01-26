package dev.atinroy.ledgerly.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BudgetCreateRequest {
    @NotNull
    private BigDecimal amount;
    @NotNull
    private Long categoryId;
}
