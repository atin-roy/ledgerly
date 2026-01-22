package dev.atinroy.financebackend.repository;

import dev.atinroy.financebackend.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByUser_UserId(Long userId, Pageable pageable);
    Page<Transaction> findByUser_UserIdAndUserTransactionType_TransactionTypeId(Long userId, Long transactionTypeId, Pageable pageable);
    Page<Transaction> findByUser_UserIdAndTransactionDateBetween(Long userId, LocalDateTime from, LocalDateTime to, Pageable pageable);
    Page<Transaction> findByUser_UserIdAndTransactionAmountBetween(Long userId, BigDecimal from, BigDecimal to, Pageable pageable);
    Page<Transaction> findByUser_UserIdAndParty_PartyId(Long userId, Long partyId, Pageable pageable);
}
