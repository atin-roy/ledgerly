package dev.atinroy.ledgerly.dto.request;

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
    private String name;
    @NotNull
    private BigDecimal target;
    @NotNull
    private BigDecimal saved;
}
