package dev.atinroy.ledgerly.service;

import dev.atinroy.ledgerly.dto.request.BudgetCreateRequest;
import dev.atinroy.ledgerly.dto.request.BudgetUpdateRequest;
import dev.atinroy.ledgerly.dto.response.BudgetResponse;
import dev.atinroy.ledgerly.entity.Budget;
import dev.atinroy.ledgerly.entity.Category;
import dev.atinroy.ledgerly.entity.User;
import dev.atinroy.ledgerly.error.*;
import dev.atinroy.ledgerly.mapper.BudgetMapper;
import dev.atinroy.ledgerly.repository.BudgetRepository;
import dev.atinroy.ledgerly.repository.CategoryRepository;
import dev.atinroy.ledgerly.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetMapper budgetMapper;

    @Transactional
    public BudgetResponse createBudget(Long userId, BudgetCreateRequest request) {
        if (request == null) {
            ValidationResult result = ValidationResult.withErrors();
            result.addGeneralError(ErrorCode.REQUIRED, "Request is required");
            throw new ValidationException(result);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Category category = categoryRepository.findByUser_IdAndId(userId, request.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(request.categoryId()));

        // Check if budget already exists for this category
        if (budgetRepository.findByUser_IdAndCategory_Id(userId, request.categoryId()).isPresent()) {
            ValidationResult result = ValidationResult.withErrors();
            result.addFieldError("categoryId", ErrorCode.ALREADY_EXISTS, "Budget already exists for this category");
            throw new ValidationException(result);
        }

        Budget budget = budgetMapper.toEntity(request);
        budget.setUser(user);
        budget.setCategory(category);

        Budget saved = budgetRepository.save(budget);
        return budgetMapper.toResponse(saved);
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
            // Check if changing to a different category
            if (!request.categoryId().equals(budget.getCategory().getId())) {
                Category newCategory = categoryRepository.findByUser_IdAndId(userId, request.categoryId())
                        .orElseThrow(() -> new CategoryNotFoundException(request.categoryId()));

                // Check if budget already exists for the new category
                if (budgetRepository.findByUser_IdAndCategory_Id(userId, request.categoryId()).isPresent()) {
                    ValidationResult result = ValidationResult.withErrors();
                    result.addFieldError("categoryId", ErrorCode.ALREADY_EXISTS, "Budget already exists for this category");
                    throw new ValidationException(result);
                }

                budget.setCategory(newCategory);
            }
        }

        Budget saved = budgetRepository.save(budget);
        return budgetMapper.toResponse(saved);
    }

    @Transactional
    public void deleteBudget(Long userId, Long budgetId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Budget budget = budgetRepository.findByUser_IdAndId(userId, budgetId)
                .orElseThrow(() -> new BudgetNotFoundException(budgetId));

        budgetRepository.delete(budget);
    }

    public BudgetResponse getBudget(Long userId, Long budgetId) {
        Budget budget = budgetRepository.findByUser_IdAndId(userId, budgetId)
                .orElseThrow(() -> new BudgetNotFoundException(budgetId));
        return budgetMapper.toResponse(budget);
    }

    public List<BudgetResponse> getBudgets(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<Budget> budgets = budgetRepository.findByUser_Id(userId);
        return budgets.stream()
                .map(budgetMapper::toResponse)
                .toList();
    }
}
