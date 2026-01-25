package dev.atinroy.ledgerly.entity;

import dev.atinroy.ledgerly.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Locale;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "user")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
@Table(
        name = "party",
        uniqueConstraints = @UniqueConstraint(columnNames = {"party_name", "user_id"}),
        indexes = @Index(name = "idx_party_party_user_name", columnList = "user_id, party_name")
)
public class Party extends BaseEntity {
    @Column(name = "party_name", nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    @PreUpdate
    private void normalizeName() {
        if (this.name != null) {
            this.name = this.name
                    .toLowerCase(Locale.ROOT)
                    .trim();
        }
    }

}
