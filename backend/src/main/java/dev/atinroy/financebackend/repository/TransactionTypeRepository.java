package dev.atinroy.financebackend.repository;

import dev.atinroy.financebackend.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionTypeRepository extends JpaRepository<TransactionType, Long> {
    Optional<TransactionType> findByUser_UserIdAndTransactionTypeId(Long userId, Long transactionTypeId);
    Optional<TransactionType> findByUser_UserIdAndTransactionTypeNameIgnoreCase(Long userId, String transactionTypeName);
}
