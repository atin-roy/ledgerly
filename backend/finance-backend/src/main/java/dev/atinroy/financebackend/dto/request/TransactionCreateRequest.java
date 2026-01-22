package dev.atinroy.financebackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionCreateRequest {
    @NotBlank
    private String partyName;
    @NotNull
    private String description;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private LocalDateTime date;
}
