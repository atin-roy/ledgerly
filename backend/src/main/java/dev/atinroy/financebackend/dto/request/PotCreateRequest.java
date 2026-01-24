package dev.atinroy.financebackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PotCreateRequest {
    @NotBlank
    private String potName;
    @NotNull
    private BigDecimal potTarget;
    @NotNull
    private BigDecimal potSaved;
    @NotNull
    private LocalDateTime targetDate;
}
