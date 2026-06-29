package dev.atinroy.ledgerly.domain.budget.repository;

import dev.atinroy.ledgerly.domain.budget.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    // Get all budgets for a user
    List<Budget> findByUser_Id(Long userId);

    // Get a specific budget with a security check
    Optional<Budget> findByUser_IdAndId(Long userId, Long budgetId);

    // Find budget by category - for validation and lookup
    Optional<Budget> findByUser_IdAndCategory_Id(Long userId, Long categoryId);

    @Modifying
    @Query("DELETE FROM Budget b WHERE b.user.id = :userId")
    void deleteByUser_Id(@Param("userId") Long userId);
}
