package dev.atinroy.ledgerly.dto.request;

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
    private String name;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private LocalDateTime dueDate;
    @NotBlank
    private String status;
}
