package dev.atinroy.ledgerly.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BudgetCreateRequest {
    @NotBlank
    @PositiveOrZero
    private BigDecimal amount;
    @NotBlank
    private Long categoryId;
}
