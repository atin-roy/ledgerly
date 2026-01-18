package dev.atinroy.financebackend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Enumerated(EnumType.STRING)
    private Long billId;
    private String billName;
    private BigDecimal billAmount;
    private LocalDateTime billDate;
    private BillStatus billStatus;
}
