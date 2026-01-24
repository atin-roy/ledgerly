package dev.atinroy.financebackend.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PotCreateResponse {
    private String potId;
    private String potName;
    private BigDecimal potTarget;
    private BigDecimal potSaved;
    private LocalDateTime createdAt;
}
