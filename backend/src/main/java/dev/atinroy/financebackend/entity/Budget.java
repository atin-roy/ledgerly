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
@Table(
        name = "budget",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "budget_name"}),
        indexes = @Index(name = "idx_budget_user_name", columnList = "user_id, budget_name")
)
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
        return budgetAmount.subtract(
                budgetSpent != null ? budgetSpent : BigDecimal.ZERO
        );
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
