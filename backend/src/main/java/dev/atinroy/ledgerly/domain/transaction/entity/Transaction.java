package dev.atinroy.ledgerly.domain.transaction.entity;

import dev.atinroy.ledgerly.domain.category.entity.Category;
import dev.atinroy.ledgerly.domain.party.entity.Party;
import dev.atinroy.ledgerly.domain.user.entity.User;
import dev.atinroy.ledgerly.shared.entity.BaseEntity;
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

    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id")
    private Party party;
}
