package dev.atinroy.ledgerly.domain.pot.entity;

import dev.atinroy.ledgerly.domain.user.entity.User;
import dev.atinroy.ledgerly.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "user")
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "pot", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "name" }))
public class Pot extends BaseEntity {
    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal target;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal saved;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
