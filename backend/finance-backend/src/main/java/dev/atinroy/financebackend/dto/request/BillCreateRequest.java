package dev.atinroy.financebackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class BillCreateRequest {
    @NotBlank
    private String billName;
    @NotNull
    private String billDescription;
    @NotNull
    private BigDecimal billAmount;
    @NotNull
    private LocalDateTime billDueDate;
    @NotBlank
    private String billStatus;
}
