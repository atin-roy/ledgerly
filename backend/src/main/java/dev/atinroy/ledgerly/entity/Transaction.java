package dev.atinroy.ledgerly.entity;

import dev.atinroy.ledgerly.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"user", "category", "party"})
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
        name = "transactions",
        indexes = {
                @Index(name = "idx_transaction_user_date", columnList = "user_id, date")
        }
)
public class Transaction extends BaseEntity {
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount; // positive = received, negative = spent

    @Column(nullable = false)
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id")
    private Party party;
}
