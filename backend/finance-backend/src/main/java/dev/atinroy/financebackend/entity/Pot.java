package dev.atinroy.financebackend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
public class Pot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long potId;
    private String potName;
    private BigDecimal potTarget;
    private BigDecimal potSaved;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
