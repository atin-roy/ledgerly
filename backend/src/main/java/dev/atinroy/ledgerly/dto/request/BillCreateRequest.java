package dev.atinroy.ledgerly.dto.request;

import dev.atinroy.ledgerly.entity.enums.BillStatus;
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
    private BillStatus status;
}
