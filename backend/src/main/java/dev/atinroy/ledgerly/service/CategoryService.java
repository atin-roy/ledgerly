package dev.atinroy.ledgerly.service;

import dev.atinroy.ledgerly.dto.request.CategoryCreateRequest;
import dev.atinroy.ledgerly.dto.request.CategoryUpdateRequest;
import dev.atinroy.ledgerly.dto.response.CategoryResponse;
import dev.atinroy.ledgerly.entity.Category;
import dev.atinroy.ledgerly.entity.User;
import dev.atinroy.ledgerly.error.*;
import dev.atinroy.ledgerly.mapper.CategoryMapper;
import dev.atinroy.ledgerly.repository.CategoryRepository;
import dev.atinroy.ledgerly.repository.UserRepository;
import dev.atinroy.ledgerly.validator.CategoryValidator;
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

    public CategoryResponse getCategory(Long userId, Long categoryId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Category category = categoryRepository.findByUser_IdAndId(userId, categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        return categoryMapper.toResponse(category);
    }

    public List<CategoryResponse> getCategories(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<Category> categories = categoryRepository.findByUser_Id(userId);
        return categories.stream()
                .map(categoryMapper::toResponse)
                .toList();
    }
}
