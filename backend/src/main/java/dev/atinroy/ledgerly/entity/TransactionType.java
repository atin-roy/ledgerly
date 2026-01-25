package dev.atinroy.ledgerly.entity;

import dev.atinroy.ledgerly.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"transactionTypeName", "user_id"}))
public class TransactionType extends BaseEntity {
    @Column(nullable = false, length = 50)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    @PreUpdate
    private void normalizeName() {
        if (this.name != null) {
            this.name = this.name.toLowerCase().trim();
        }
    }
}
