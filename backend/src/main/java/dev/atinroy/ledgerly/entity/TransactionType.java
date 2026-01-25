package dev.atinroy.ledgerly.entity;

import dev.atinroy.ledgerly.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "user")
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name", "user_id"}))
public class TransactionType extends BaseEntity {
    @Column(nullable = false, length = 50)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
