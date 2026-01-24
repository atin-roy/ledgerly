package dev.atinroy.financebackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Locale;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "user")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(
        name = "party",
        uniqueConstraints = @UniqueConstraint(columnNames = {"party_name", "user_id"}),
        indexes = @Index(name = "idx_party_party_user_name", columnList = "user_id, party_name")
)
public class Party {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long partyId;

    @Column(name = "party_name", nullable = false, length = 100)
    private String partyName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
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
        if (this.partyName != null) {
            this.partyName = this.partyName
                    .toLowerCase(Locale.ROOT)
                    .trim();
        }
    }

}
