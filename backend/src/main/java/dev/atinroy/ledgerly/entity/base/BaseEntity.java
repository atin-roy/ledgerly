package dev.atinroy.ledgerly.entity.base;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    protected LocalDateTime createdAt;

    @Column(nullable = false)
    @UpdateTimestamp
    protected LocalDateTime updatedAt;

    protected LocalDateTime deletedAt;
}
