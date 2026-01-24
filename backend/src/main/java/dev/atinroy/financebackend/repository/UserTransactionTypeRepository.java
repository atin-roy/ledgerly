package dev.atinroy.financebackend.repository;

import dev.atinroy.financebackend.entity.UserTransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserTransactionTypeRepository extends JpaRepository<UserTransactionType, Long> {
    Optional<UserTransactionType> findByUser_UserIdAndTransactionTypeId(Long userId, Long transactionTypeId);
    Optional<UserTransactionType> findByUser_UserIdAndTransactionTypeNameIgnoreCase(Long userId, String transactionTypeName);
}
