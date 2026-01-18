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

    private Long billId;
    private String billName;
    private BigDecimal billAmount;
    private LocalDateTime billDate;

    @Enumerated(EnumType.STRING)
    private BillStatus billStatus;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
