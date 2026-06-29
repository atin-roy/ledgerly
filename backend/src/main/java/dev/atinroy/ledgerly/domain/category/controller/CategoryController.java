package dev.atinroy.ledgerly.domain.category.controller;

import dev.atinroy.ledgerly.domain.category.dto.CategoryCreateRequest;
import dev.atinroy.ledgerly.domain.category.dto.CategoryUpdateRequest;
import dev.atinroy.ledgerly.domain.category.dto.CategoryResponse;
import dev.atinroy.ledgerly.security.AuthenticatedUserId;
import dev.atinroy.ledgerly.domain.category.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @AuthenticatedUserId Long authenticatedUserId,
            @PathVariable Long userId,
            @Valid @RequestBody CategoryCreateRequest request) {
        requireOwnership(authenticatedUserId, userId);
        CategoryResponse response = categoryService.createCategory(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getCategories(
            @AuthenticatedUserId Long authenticatedUserId,
            @PathVariable Long userId) {
        requireOwnership(authenticatedUserId, userId);
        List<CategoryResponse> responses = categoryService.getCategories(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> getCategory(
            @AuthenticatedUserId Long authenticatedUserId,
            @PathVariable Long userId,
            @PathVariable Long categoryId) {
        requireOwnership(authenticatedUserId, userId);
        CategoryResponse response = categoryService.getCategory(userId, categoryId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @AuthenticatedUserId Long authenticatedUserId,
            @PathVariable Long userId,
            @PathVariable Long categoryId,
            @Valid @RequestBody CategoryUpdateRequest request) {
        requireOwnership(authenticatedUserId, userId);
        CategoryResponse response = categoryService.updateCategory(userId, categoryId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(
            @AuthenticatedUserId Long authenticatedUserId,
            @PathVariable Long userId,
            @PathVariable Long categoryId) {
        requireOwnership(authenticatedUserId, userId);
        categoryService.deleteCategory(userId, categoryId);
        return ResponseEntity.noContent().build();
    }

    private void requireOwnership(Long authenticatedUserId, Long userId) {
        if (!authenticatedUserId.equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }
    }
}
