package dev.atinroy.ledgerly.entity;

import dev.atinroy.ledgerly.entity.base.BaseEntity;
import dev.atinroy.ledgerly.entity.enums.BillStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "user")
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
        name = "bill",
        indexes = @Index(name = "idx_bill_due_date", columnList = "due_date")
)
public class Bill extends BaseEntity {
    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BillStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
