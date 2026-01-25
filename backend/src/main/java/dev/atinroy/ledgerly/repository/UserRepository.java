package dev.atinroy.ledgerly.repository;

import dev.atinroy.ledgerly.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Page<User> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);
}
