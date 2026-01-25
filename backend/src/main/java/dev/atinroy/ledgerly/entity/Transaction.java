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
        name = "transactions",
        indexes = {
                @Index(name = "idx_transaction_date", columnList = "transaction_date"),
                @Index(name = "idx_transaction_user_date", columnList = "user_id, transaction_date")
        }
)
public class Transaction extends BaseEntity {
    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(length = 255)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_type_id", nullable = false)
    private TransactionType type;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id") // nullable because not all transactions involve a party
    private Party party;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id") // nullable because not all transactions are associated with a budget
    private Budget budget;

    @PrePersist
    void prePersist() {
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }
    }

}
