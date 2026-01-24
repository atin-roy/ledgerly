package dev.atinroy.financebackend.repository;

import dev.atinroy.financebackend.entity.SystemTransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SystemTransactionTypeRepository extends JpaRepository<SystemTransactionType, Long> {
    Optional<SystemTransactionType> findBySystemTransactionName(String systemTransactionName);
}
