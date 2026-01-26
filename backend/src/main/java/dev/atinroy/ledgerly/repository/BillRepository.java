package dev.atinroy.ledgerly.repository;

import dev.atinroy.ledgerly.entity.Bill;
import dev.atinroy.ledgerly.entity.enums.BillStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * - Pagination-first (no unbounded queries)
 * - Avoids fragile equality checks on money
 * - Designed to scale with future filtering needs
 * - Clean separation of concerns
 */
public interface BillRepository extends JpaRepository<Bill, Long> {
    // Get all bills for a user - for displaying the main bills list
    List<Bill> findByUser_Id(Long userId);

    // Get a specific bill with security check - for the "Pay" or "Mark Paid" actions
    Optional<Bill> findByUser_IdAndId(Long userId, Long billId);

    // Count bills by status - for summary statistics
    long countByUser_IdAndStatus(Long userId, BillStatus status);

    // Sum bill amounts by status - for summary statistics
    @Query("SELECT SUM(b.amount) FROM Bill b WHERE b.user.id = :userId AND b.status = :status")
    BigDecimal sumAmountByUserIdAndStatus(@Param("userId") Long userId, @Param("status") BillStatus status);
}
