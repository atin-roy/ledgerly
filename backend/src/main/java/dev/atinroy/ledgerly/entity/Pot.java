package dev.atinroy.ledgerly.entity;

import dev.atinroy.ledgerly.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "pot",
        uniqueConstraints = @UniqueConstraint(columnNames = {"pot_name", "user_id"}),
        indexes = @Index(name = "idx_pot_pot_user_name", columnList = "user_id, pot_name")
)
public class Pot extends BaseEntity {
    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal target;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal saved;

    @Column()
    private LocalDateTime targetDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
