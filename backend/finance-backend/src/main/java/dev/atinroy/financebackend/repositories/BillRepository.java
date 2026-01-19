package dev.atinroy.financebackend.repositories;

import dev.atinroy.financebackend.entity.Bill;
import dev.atinroy.financebackend.repositories.BillSuggestion;
import dev.atinroy.financebackend.entity.BillStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * - Pagination-first (no unbounded queries)
 * - Avoids fragile equality checks on money
 * - Designed to scale with future filtering needs
 * - Clean separation of concerns
 */
public interface BillRepository extends JpaRepository<Bill, Long> {

    /**
     * Base query: fetch all bills for a user.
     * This is intentionally the simplest query.
     * Other filters build conceptually on top of this.
     */
    Page<Bill> findByUser_Id(Long userId, Pageable pageable);

    /**
     * Fetch bills for a user filtered by name.
     * Uses fuzzy search
     */
    Page<BillSuggestion> findByUser_IdAndBillNameContainingIgnoreCase(
        Long userId,
        String query,
        Pageable pageable
    );



    /**
     * MONEY HANDLING (IMPORTANT)
     * --------------------------
     * Avoid exact equality on monetary values in real systems.
     *
     * Instead of:
     *   billAmount = X
     *
     * We prefer:
     *   billAmount BETWEEN min AND max
     *
     * This avoids:
     * - rounding issues
     * - scale mismatches
     * - currency representation problems
     */
    Page<Bill> findByUser_IdAndBillAmountBetween(
        Long userId,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        Pageable pageable
    );

    /**
     * Fetch bills by status.
     *
     * Status enums are stable and index-friendly.
     * This query scales well even with large datasets.
     */
    Page<Bill> findByUser_IdAndBillStatus(
        Long userId,
        BillStatus billStatus,
        Pageable pageable
    );

    /**
     * Fetch bills created within a date range.
     *
     * Designed for:
     * - monthly reports
     * - statements
     * - analytics views
     *
     * BETWEEN is inclusive:
     *   startDate <= createdAt <= endDate
     */
    Page<Bill> findByUser_IdAndCreatedAtBetween(
        Long userId,
        LocalDate startDate,
        LocalDate endDate,
        Pageable pageable
    );
}
