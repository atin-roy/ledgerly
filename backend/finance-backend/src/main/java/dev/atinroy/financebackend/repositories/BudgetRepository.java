package dev.atinroy.financebackend.repositories;

import dev.atinroy.financebackend.entity.Budget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BudgetRepository {
    Page<Budget> findByUser_UserId(Long userId, Pageable pageable);
}
