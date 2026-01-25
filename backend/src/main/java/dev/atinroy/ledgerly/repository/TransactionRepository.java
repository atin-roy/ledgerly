package dev.atinroy.ledgerly.repository;

import dev.atinroy.ledgerly.entity.Transaction;
import dev.atinroy.ledgerly.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByTransactionIdAndUser_UserId(Long userId, Long transactionId);
    Page<Transaction> findByUser_UserId(Long userId, Pageable pageable);
    Page<Transaction> findByUser_UserIdAndType_TransactionTypeId(Long userId, Long transactionTypeId, Pageable pageable);
    Page<Transaction> findByUser_UserIdAndDateBetween(Long userId, LocalDateTime from, LocalDateTime to, Pageable pageable);
    Page<Transaction> findByUser_UserIdAndAmountBetween(Long userId, BigDecimal from, BigDecimal to, Pageable pageable);
    Page<Transaction> findByUser_UserIdAndParty_PartyId(Long userId, Long partyId, Pageable pageable);
    Page<Transaction> findByUser_UserIdAndBudget_BudgetId(Long userId, Long budgetId, Pageable pageable);

    List<Transaction> user(User user);
}
