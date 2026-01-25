package dev.atinroy.ledgerly.entity;

import dev.atinroy.ledgerly.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "user")
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
        name = "party",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "user_id"}),
        indexes = @Index(name = "idx_party_user_name", columnList = "user_id, name")
)
public class Party extends BaseEntity {
    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
