package dev.atinroy.financebackend.repositories;

import dev.atinroy.financebackend.entity.Budget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Page<Budget> findByUser_UserId(Long userId, Pageable pageable);
}
