package dev.atinroy.ledgerly.controller;

import dev.atinroy.ledgerly.dto.request.BudgetCreateRequest;
import dev.atinroy.ledgerly.dto.request.BudgetUpdateRequest;
import dev.atinroy.ledgerly.dto.response.BudgetResponse;
import dev.atinroy.ledgerly.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    public ResponseEntity<BudgetResponse> createBudget(
            @PathVariable Long userId,
            @RequestBody BudgetCreateRequest request) {
        BudgetResponse response = budgetService.createBudget(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getBudgets(@PathVariable Long userId) {
        List<BudgetResponse> responses = budgetService.getBudgets(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{budgetId}")
    public ResponseEntity<BudgetResponse> getBudget(
            @PathVariable Long userId,
            @PathVariable Long budgetId) {
        BudgetResponse response = budgetService.getBudget(userId, budgetId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{budgetId}")
    public ResponseEntity<BudgetResponse> updateBudget(
            @PathVariable Long userId,
            @PathVariable Long budgetId,
            @RequestBody BudgetUpdateRequest request) {
        BudgetResponse response = budgetService.updateBudget(userId, budgetId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{budgetId}")
    public ResponseEntity<Void> deleteBudget(
            @PathVariable Long userId,
            @PathVariable Long budgetId) {
        budgetService.deleteBudget(userId, budgetId);
        return ResponseEntity.noContent().build();
    }
}
