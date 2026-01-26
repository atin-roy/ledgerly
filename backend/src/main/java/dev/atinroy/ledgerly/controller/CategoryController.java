package dev.atinroy.ledgerly.controller;

import dev.atinroy.ledgerly.dto.request.CategoryCreateRequest;
import dev.atinroy.ledgerly.dto.request.CategoryUpdateRequest;
import dev.atinroy.ledgerly.dto.response.CategoryResponse;
import dev.atinroy.ledgerly.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @PathVariable Long userId,
            @RequestBody CategoryCreateRequest request) {
        CategoryResponse response = categoryService.createCategory(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getCategories(@PathVariable Long userId) {
        List<CategoryResponse> responses = categoryService.getCategories(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> getCategory(
            @PathVariable Long userId,
            @PathVariable Long categoryId) {
        CategoryResponse response = categoryService.getCategory(userId, categoryId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long userId,
            @PathVariable Long categoryId,
            @RequestBody CategoryUpdateRequest request) {
        CategoryResponse response = categoryService.updateCategory(userId, categoryId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long userId,
            @PathVariable Long categoryId) {
        categoryService.deleteCategory(userId, categoryId);
        return ResponseEntity.noContent().build();
    }
}
