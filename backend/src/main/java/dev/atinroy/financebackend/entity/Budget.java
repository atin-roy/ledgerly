package dev.atinroy.financebackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(indexes = {
        @Index(name = "idx_budget_budget_name", columnList = "budget_name")
})
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long budgetId;

    @Column(nullable = false, length = 100)
    private String budgetName;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal budgetAmount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal budgetSpent;

    @Transient
    public BigDecimal getBudgetRemaining() {
        return budgetAmount.subtract(budgetSpent);
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
