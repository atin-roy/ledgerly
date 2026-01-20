package dev.atinroy.financebackend.repositories;

import dev.atinroy.financebackend.entity.Bill;
import dev.atinroy.financebackend.entity.BillStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * - Pagination-first (no unbounded queries)
 * - Avoids fragile equality checks on money
 * - Designed to scale with future filtering needs
 * - Clean separation of concerns
 */
public interface BillRepository extends JpaRepository<Bill, Long> {

    Page<Bill> findByUser_UserId(Long userId, Pageable pageable);

    Page<BillSuggestion> findByUser_UserIdAndBillNameContainingIgnoreCase(
        Long userId,
        String query,
        Pageable pageable
    );

    Page<Bill> findByUser_IdAndBillAmountBetween(
        Long userId,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        Pageable pageable
    );

    Page<Bill> findByUser_UserIdAndBillStatus(
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
