package dev.atinroy.financebackend.repositories;

import dev.atinroy.financebackend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface UserRepository extends JpaRepository<User, Long> {
    Page<User> findByUserId(Long userId, Pageable pageable);
    Page<User> findByUsername(String username, Pageable pageable);
    Page<User> findByEmail(String email, Pageable pageable);
    Page<User> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);
}
