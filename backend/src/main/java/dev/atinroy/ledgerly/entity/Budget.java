package dev.atinroy.ledgerly.entity;

import dev.atinroy.ledgerly.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "user")
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
        name = "budget",
        uniqueConstraints = @UniqueConstraint(columnNames = {"category_id"})
)
public class Budget extends BaseEntity {
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @OneToOne(optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category; // Each budget tracks ONE category

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
