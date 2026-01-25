package dev.atinroy.ledgerly.entity;

import dev.atinroy.ledgerly.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "budget",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "budget_name"}),
        indexes = @Index(name = "idx_budget_user_name", columnList = "user_id, budget_name")
)
public class Budget extends BaseEntity {
    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal spent;

    @Transient
    public BigDecimal getRemaining() {
        return amount.subtract(
                spent != null ? spent : BigDecimal.ZERO
        );
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
