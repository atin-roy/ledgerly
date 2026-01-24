package dev.atinroy.financebackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "system_transaction_type")
public class SystemTransactionType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long systemTransactionId;

    @Column(nullable = false, unique = true)
    private String systemTransactionName;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
