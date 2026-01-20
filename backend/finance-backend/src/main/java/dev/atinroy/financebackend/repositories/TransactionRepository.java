package dev.atinroy.financebackend.repositories;

import dev.atinroy.financebackend.entity.Party;
import dev.atinroy.financebackend.entity.Transaction;
import dev.atinroy.financebackend.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByUser_UserId(Long userId, Pageable pageable);
    Page<Transaction> findByUser_UserIdAndTransactionType(Long userId, TransactionType transactionType, Pageable pageable);
    Page<Transaction> findByUser_UserIdAndTransactionDateBetween(Long userId, LocalDateTime from, LocalDateTime to, Pageable pageable);
    Page<Transaction> findByUser_UserIdAndTransactionAmountBetween(Long userId, BigDecimal from, BigDecimal to, Pageable pageable);
    Page<Transaction> findByUser_UserIdAndParty(Long UserId, Party party, Pageable pageable);
}
