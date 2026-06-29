package dev.atinroy.ledgerly.domain.category.entity;

import dev.atinroy.ledgerly.domain.budget.entity.Budget;
import dev.atinroy.ledgerly.domain.user.entity.User;
import dev.atinroy.ledgerly.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "user")
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name", "user_id"}))
public class Category extends BaseEntity {
    @Column(nullable = false, length = 50)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(mappedBy = "category")
    private Budget budget;  // Nullable - not all categories have budgets
}
