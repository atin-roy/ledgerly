package dev.atinroy.ledgerly.repository;

import dev.atinroy.ledgerly.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    // Get all budgets for a user
    List<Budget> findByUser_Id(Long userId);

    // Get a specific budget with a security check
    Optional<Budget> findByUser_IdAndId(Long userId, Long budgetId);

    // Find budget by category - for validation and lookup
    Optional<Budget> findByUser_IdAndCategory_Id(Long userId, Long categoryId);
}
