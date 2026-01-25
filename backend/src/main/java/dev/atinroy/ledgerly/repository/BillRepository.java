package dev.atinroy.ledgerly.repository;

import dev.atinroy.ledgerly.entity.Bill;
import dev.atinroy.ledgerly.entity.enums.BillStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * - Pagination-first (no unbounded queries)
 * - Avoids fragile equality checks on money
 * - Designed to scale with future filtering needs
 * - Clean separation of concerns
 */
public interface BillRepository extends JpaRepository<Bill, Long> {

    Page<Bill> findByUser_UserId(Long userId, Pageable pageable);

    Page<Bill> findByUser_UserIdAndNameContainingIgnoreCase(
        Long userId,
        String query,
        Pageable pageable
    );

    Page<Bill> findByUser_UserIdAndAmountBetween(
        Long userId,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        Pageable pageable
    );

    Page<Bill> findByUser_UserIdAndStatus(
        Long userId,
        BillStatus billStatus,
        Pageable pageable
    );

    Page<Bill> findByUser_UserIdAndCreatedAtBetween(
        Long userId,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Pageable pageable
    );
}
