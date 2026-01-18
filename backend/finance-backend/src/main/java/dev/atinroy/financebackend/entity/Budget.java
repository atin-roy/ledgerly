package dev.atinroy.financebackend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private BigDecimal budgetRemaining;
}
