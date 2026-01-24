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
        name = "transactions",
        indexes = {
                @Index(name = "idx_transaction_date", columnList = "transaction_date"),
                @Index(name = "idx_transaction_user_date", columnList = "user_id, transaction_date")
        }
)
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @Column(nullable = false)
    private LocalDateTime transactionDate;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal transactionAmount;

    @Column(length = 255)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_type_id", nullable = false)
    private TransactionType transactionType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id") // nullable because not all transactions involve a party
    private Party party;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id") // nullable because not all transactions are associated with a budget
    private Budget budget;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        if (transactionAmount == null) {
            transactionAmount = BigDecimal.ZERO;
        }
    }

}
