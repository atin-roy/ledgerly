package dev.atinroy.ledgerly.repository;

import dev.atinroy.ledgerly.entity.Pot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PotRepository extends JpaRepository<Pot, Long> {
    Page<Pot> findByUser_UserId(Long userId, Pageable pageable);
    Page<Pot> findByUser_UserIdAndName(Long userId, String potName, Pageable pageable);
}
