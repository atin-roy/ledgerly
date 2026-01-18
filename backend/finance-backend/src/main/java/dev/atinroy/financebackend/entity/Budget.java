package dev.atinroy.financebackend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long budgetId;
    private String budgetName;
    private BigDecimal budgetAmount;
    private BigDecimal budgetSpent;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Transient
    public BigDecimal getBudgetRemaining() {
        return budgetAmount.subtract(budgetSpent);
    }
}
