package dev.atinroy.ledgerly.domain.budget.service;

import dev.atinroy.ledgerly.domain.budget.exception.BudgetNotFoundException;
import dev.atinroy.ledgerly.domain.user.exception.UserNotFoundException;
import dev.atinroy.ledgerly.domain.budget.dto.BudgetCreateRequest;
import dev.atinroy.ledgerly.domain.budget.dto.BudgetUpdateRequest;
import dev.atinroy.ledgerly.domain.budget.dto.BudgetResponse;
import dev.atinroy.ledgerly.domain.budget.entity.Budget;
import dev.atinroy.ledgerly.domain.category.entity.Category;
import dev.atinroy.ledgerly.domain.category.service.CategoryService;
import dev.atinroy.ledgerly.domain.user.entity.User;
import dev.atinroy.ledgerly.shared.error.*;
import dev.atinroy.ledgerly.domain.budget.mapper.BudgetMapper;
import dev.atinroy.ledgerly.domain.budget.repository.BudgetRepository;
import dev.atinroy.ledgerly.domain.transaction.repository.TransactionRepository;
import dev.atinroy.ledgerly.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final CategoryService categoryService;
    private final BudgetMapper budgetMapper;
    private final TransactionRepository transactionRepository;

    @Transactional
    public BudgetResponse createBudget(Long userId, BudgetCreateRequest request) {
        if (request == null) {
            ValidationResult result = ValidationResult.withErrors();
            result.addGeneralError(ErrorCode.REQUIRED, "Request is required");
            throw new ValidationException(result);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Category category = categoryService.findOrResolveGeneralCategory(userId, request.categoryId());

        if (budgetRepository.findByUser_IdAndCategory_Id(userId, category.getId()).isPresent()) {
            ValidationResult result = ValidationResult.withErrors();
            result.addFieldError("categoryId", ErrorCode.ALREADY_EXISTS, "Budget already exists for this category");
            throw new ValidationException(result);
        }

        Budget budget = budgetMapper.toEntity(request);
        budget.setUser(user);
        budget.setCategory(category);

        Budget saved = budgetRepository.save(budget);
        return toResponse(saved, BigDecimal.ZERO);
    }

    @Transactional
    public BudgetResponse updateBudget(Long userId, Long budgetId, BudgetUpdateRequest request) {
        if (request == null) {
            ValidationResult result = ValidationResult.withErrors();
            result.addGeneralError(ErrorCode.REQUIRED, "Request is required");
            throw new ValidationException(result);
        }

        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Budget budget = budgetRepository.findByUser_IdAndId(userId, budgetId)
                .orElseThrow(() -> new BudgetNotFoundException(budgetId));

        if (request.amount() != null) {
            budget.setAmount(request.amount());
        }

        if (request.categoryId() != null) {
            Category newCategory = categoryService.findOrResolveGeneralCategory(userId, request.categoryId());

            if (!newCategory.getId().equals(budget.getCategory().getId())) {
                if (budgetRepository.findByUser_IdAndCategory_Id(userId, newCategory.getId()).isPresent()) {
                    ValidationResult result = ValidationResult.withErrors();
                    result.addFieldError("categoryId", ErrorCode.ALREADY_EXISTS, "Budget already exists for this category");
                    throw new ValidationException(result);
                }
                budget.setCategory(newCategory);
            }
        }

        Budget saved = budgetRepository.save(budget);
        return toResponse(saved, BigDecimal.ZERO);
    }

    @Transactional
    public void deleteBudget(Long userId, Long budgetId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Budget budget = budgetRepository.findByUser_IdAndId(userId, budgetId)
                .orElseThrow(() -> new BudgetNotFoundException(budgetId));

        budgetRepository.delete(budget);
    }

    @Transactional(readOnly = true)
    public BudgetResponse getBudget(Long userId, Long budgetId) {
        Budget budget = budgetRepository.findByUser_IdAndId(userId, budgetId)
                .orElseThrow(() -> new BudgetNotFoundException(budgetId));
        BigDecimal spent = transactionRepository.sumSpentByCategoryId(userId, budget.getCategory().getId());
        return toResponse(budget, spent != null ? spent : BigDecimal.ZERO);
    }

    @Transactional(readOnly = true)
    public List<BudgetResponse> getBudgets(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<Budget> budgets = budgetRepository.findByUser_Id(userId);

        Map<Long, BigDecimal> spendingMap = transactionRepository
                .sumSpendingByCategoryForUser(userId)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (BigDecimal) row[1]
                ));

        return budgets.stream()
                .map(b -> toResponse(b, spendingMap.getOrDefault(b.getCategory().getId(), BigDecimal.ZERO)))
                .toList();
    }

    private BudgetResponse toResponse(Budget budget, BigDecimal spent) {
        return new BudgetResponse(
                budget.getId(),
                budget.getAmount(),
                budget.getCategory().getId(),
                spent
        );
    }
}
