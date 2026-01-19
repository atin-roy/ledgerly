package dev.atinroy.financebackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionTypeId;

    @Column(nullable = false, unique = true, length = 50)
    private String transactionTypeName;

    // ADD THIS:
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true) // null = system-wide category
    private User user;

    @Column(nullable = false)
    private Boolean isSystemCategory = false; // true for INCOME, EXPENSE, etc.

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
