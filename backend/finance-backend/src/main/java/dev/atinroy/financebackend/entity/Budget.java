package dev.atinroy.financebackend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long budgetId;
    private String budgetName;
    private Double budgetAmount;
    private Double budgetSpent;
    private Double budgetRemaining;
}
