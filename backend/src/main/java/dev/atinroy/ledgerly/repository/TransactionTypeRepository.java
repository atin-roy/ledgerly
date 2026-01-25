package dev.atinroy.ledgerly.repository;

import dev.atinroy.ledgerly.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionTypeRepository extends JpaRepository<TransactionType, Long> {
    Optional<TransactionType> findByUser_UserIdAndTransactionTypeId(Long userId, Long transactionTypeId);
    Optional<TransactionType> findByUser_UserIdAndNameIgnoreCase(Long userId, String transactionTypeName);
}
