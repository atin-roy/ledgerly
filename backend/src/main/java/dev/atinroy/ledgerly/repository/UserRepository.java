package dev.atinroy.ledgerly.repository;

import dev.atinroy.ledgerly.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndDeletedAtIsNull(String email);
    Page<User> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);
    @Modifying
    @Query("UPDATE User u SET u.deletedAt = :now WHERE u.id = :userId")
    int softDeleteById(@Param("userId") Long userId,
                       @Param("now") LocalDateTime now);
}
