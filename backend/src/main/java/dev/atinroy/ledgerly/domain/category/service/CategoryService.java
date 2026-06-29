package dev.atinroy.ledgerly.domain.category.service;

import dev.atinroy.ledgerly.domain.category.exception.CategoryNotFoundException;
import dev.atinroy.ledgerly.domain.user.exception.UserNotFoundException;
import dev.atinroy.ledgerly.domain.category.dto.CategoryCreateRequest;
import dev.atinroy.ledgerly.domain.category.dto.CategoryUpdateRequest;
import dev.atinroy.ledgerly.domain.category.dto.CategoryResponse;
import dev.atinroy.ledgerly.domain.category.entity.Category;
import dev.atinroy.ledgerly.domain.user.entity.User;
import dev.atinroy.ledgerly.shared.error.*;
import dev.atinroy.ledgerly.domain.category.mapper.CategoryMapper;
import dev.atinroy.ledgerly.domain.category.repository.CategoryRepository;
import dev.atinroy.ledgerly.domain.user.repository.UserRepository;
import dev.atinroy.ledgerly.domain.category.validator.CategoryValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CategoryMapper categoryMapper;
    private final CategoryValidator categoryValidator;

    @Transactional
    public CategoryResponse createCategory(Long userId, CategoryCreateRequest request) {
        if (request == null) {
            ValidationResult result = ValidationResult.withErrors();
            result.addGeneralError(ErrorCode.REQUIRED, "Request is required");
            throw new ValidationException(result);
        }

        ValidationResult result = categoryValidator.validate(request);

        if (result.hasErrors()) {
            throw new ValidationException(result);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Check if category with same name already exists for this user
        if (categoryRepository.existsByUser_IdAndName(userId, request.name())) {
            result.addFieldError(
                    "name",
                    ErrorCode.ALREADY_EXISTS,
                    "Category with this name already exists"
            );
            throw new ValidationException(result);
        }

        Category category = categoryMapper.toEntity(request);
        category.setUser(user);

        Category saved = categoryRepository.save(category);
        return categoryMapper.toResponse(saved);
    }

    @Transactional
    public CategoryResponse updateCategory(Long userId, Long categoryId, CategoryUpdateRequest request) {
        if (request == null) {
            ValidationResult result = ValidationResult.withErrors();
            result.addGeneralError(ErrorCode.REQUIRED, "Request is required");
            throw new ValidationException(result);
        }

        ValidationResult result = ValidationResult.withErrors();

        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Category category = categoryRepository.findByUser_IdAndId(userId, categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        // Check if new name already exists for this user (but allow same name)
        if (request.name() != null && !request.name().equals(category.getName())) {
            if (categoryRepository.existsByUser_IdAndName(userId, request.name())) {
                result.addFieldError(
                        "name",
                        ErrorCode.ALREADY_EXISTS,
                        "Category with this name already exists"
                );
                throw new ValidationException(result);
            }
            category.setName(request.name());
        }

        Category saved = categoryRepository.save(category);
        return categoryMapper.toResponse(saved);
    }

    @Transactional
    public void deleteCategory(Long userId, Long categoryId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Category category = categoryRepository.findByUser_IdAndId(userId, categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        categoryRepository.delete(category);
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategory(Long userId, Long categoryId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Category category = categoryRepository.findByUser_IdAndId(userId, categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        return categoryMapper.toResponse(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategories(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<Category> categories = categoryRepository.findByUser_Id(userId);
        return categories.stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Category findOrResolveGeneralCategory(Long userId, Long categoryId) {
        // If categoryId is 0, resolve to the user's "General" category
        if (categoryId == 0) {
            return categoryRepository.findByUser_IdAndName(userId, "General")
                    .orElseThrow(() -> new CategoryNotFoundException(0L));
        }

        // Otherwise, find the category normally
        return categoryRepository.findByUser_IdAndId(userId, categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
    }
}
