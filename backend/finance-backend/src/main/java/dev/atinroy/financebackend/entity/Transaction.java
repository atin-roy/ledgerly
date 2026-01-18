package dev.atinroy.financebackend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;
    @ManyToOne
    @JoinColumn(name = "transaction_type")
    private TransactionType transactionType;
    private LocalDateTime transactionDate;
    private BigDecimal transactionAmount;
}
