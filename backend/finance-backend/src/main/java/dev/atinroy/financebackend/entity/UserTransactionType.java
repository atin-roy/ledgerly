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
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"transactionTypeName", "user_id"}))
public class UserTransactionType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionTypeId;

    @Column(nullable = false, length = 50)
    private String transactionTypeName;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true) // null = system-wide category
    private User user;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    private void normalizeName() {
        if (this.transactionTypeName != null) {
            this.transactionTypeName = this.transactionTypeName.toLowerCase().trim();
        }
    }
}
