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
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;
    @ManyToOne
    @JoinColumn(name = "transaction_type_id")
    private TransactionType transactionType;
    @Column(nullable = false)
    private LocalDateTime transactionDate;
    @Column(nullable = false)
    private BigDecimal transactionAmount;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // ADD THIS:
    @ManyToOne
    @JoinColumn(name = "party_id", nullable = true) // nullable because not all transactions involve a party
    private Party party;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
