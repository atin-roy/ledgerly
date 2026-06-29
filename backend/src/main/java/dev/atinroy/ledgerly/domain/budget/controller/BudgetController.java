package dev.atinroy.ledgerly.domain.budget.controller;

import dev.atinroy.ledgerly.domain.budget.dto.BudgetCreateRequest;
import dev.atinroy.ledgerly.domain.budget.dto.BudgetUpdateRequest;
import dev.atinroy.ledgerly.domain.budget.dto.BudgetResponse;
import dev.atinroy.ledgerly.security.AuthenticatedUserId;
import dev.atinroy.ledgerly.domain.budget.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    public ResponseEntity<BudgetResponse> createBudget(
            @AuthenticatedUserId Long authenticatedUserId,
            @PathVariable Long userId,
            @Valid @RequestBody BudgetCreateRequest request) {
        requireOwnership(authenticatedUserId, userId);
        BudgetResponse response = budgetService.createBudget(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getBudgets(
            @AuthenticatedUserId Long authenticatedUserId,
            @PathVariable Long userId) {
        requireOwnership(authenticatedUserId, userId);
        List<BudgetResponse> responses = budgetService.getBudgets(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{budgetId}")
    public ResponseEntity<BudgetResponse> getBudget(
            @AuthenticatedUserId Long authenticatedUserId,
            @PathVariable Long userId,
            @PathVariable Long budgetId) {
        requireOwnership(authenticatedUserId, userId);
        BudgetResponse response = budgetService.getBudget(userId, budgetId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{budgetId}")
    public ResponseEntity<BudgetResponse> updateBudget(
            @AuthenticatedUserId Long authenticatedUserId,
            @PathVariable Long userId,
            @PathVariable Long budgetId,
            @Valid @RequestBody BudgetUpdateRequest request) {
        requireOwnership(authenticatedUserId, userId);
        BudgetResponse response = budgetService.updateBudget(userId, budgetId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{budgetId}")
    public ResponseEntity<Void> deleteBudget(
            @AuthenticatedUserId Long authenticatedUserId,
            @PathVariable Long userId,
            @PathVariable Long budgetId) {
        requireOwnership(authenticatedUserId, userId);
        budgetService.deleteBudget(userId, budgetId);
        return ResponseEntity.noContent().build();
    }

    private void requireOwnership(Long authenticatedUserId, Long userId) {
        if (!authenticatedUserId.equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }
    }
}
