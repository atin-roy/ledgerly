package dev.atinroy.ledgerly.repository;

import dev.atinroy.ledgerly.entity.Budget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Page<Budget> findByUser_UserId(Long userId, Pageable pageable);
    Optional<Budget> findByUser_UserIdAndBudgetId(Long userId, Long budgetId);
}
