package dev.atinroy.financebackend.repository;

import dev.atinroy.financebackend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(Long userId);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Page<User> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);
}
