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
        uniqueConstraints = @UniqueConstraint(columnNames = {"normalized_name", "user_id"})
)
public class Party extends BaseEntity {
    // Display name - preserves user's original capitalization
    @Column(nullable = false, length = 100)
    private String name;

    // Normalized name - used for searching and uniqueness
    // Always lowercase, trimmed, used in queries
    @Column(name = "normalized_name", nullable = false, length = 100)
    private String normalizedName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public static String normalize(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Party name cannot be null");
        }

        return name.trim()                    // Remove leading/trailing whitespace
                .toLowerCase()              // Convert to lowercase
                .replaceAll("\\s+", " ");  // Collapse multiple spaces to one
    }
}
