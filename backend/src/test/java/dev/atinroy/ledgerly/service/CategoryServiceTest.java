package dev.atinroy.ledgerly.service;

import dev.atinroy.ledgerly.dto.request.CategoryCreateRequest;
import dev.atinroy.ledgerly.dto.request.CategoryUpdateRequest;
import dev.atinroy.ledgerly.dto.response.CategoryResponse;
import dev.atinroy.ledgerly.entity.Category;
import dev.atinroy.ledgerly.entity.User;
import dev.atinroy.ledgerly.entity.enums.UserRole;
import dev.atinroy.ledgerly.error.CategoryNotFoundException;
import dev.atinroy.ledgerly.error.UserNotFoundException;
import dev.atinroy.ledgerly.error.ValidationException;
import dev.atinroy.ledgerly.error.ValidationResult;
import dev.atinroy.ledgerly.mapper.CategoryMapper;
import dev.atinroy.ledgerly.repository.CategoryRepository;
import dev.atinroy.ledgerly.repository.UserRepository;
import dev.atinroy.ledgerly.validator.CategoryValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CategoryService.
 *
 * CategoryService manages expense categories with:
 * - Unique names per user
 * - Special "General" category resolution (ID 0)
 * - Standard CRUD operations
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService Tests")
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private CategoryValidator categoryValidator;

    @InjectMocks
    private CategoryService categoryService;

    // Test fixtures
    private User testUser;
    private Category testCategory;
    private Category generalCategory;
    private CategoryResponse testCategoryResponse;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setRole(UserRole.USER);

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Groceries");
        testCategory.setUser(testUser);

        generalCategory = new Category();
        generalCategory.setId(100L);
        generalCategory.setName("General");
        generalCategory.setUser(testUser);

        // CategoryResponse(id, name)
        testCategoryResponse = new CategoryResponse(1L, "Groceries");
    }

    // ==================== Create Category Tests ====================

    @Nested
    @DisplayName("Create Category")
    class CreateCategory {

        @Test
        @DisplayName("should create category successfully")
        void shouldCreateCategorySuccessfully() {
            // Arrange
            CategoryCreateRequest request = new CategoryCreateRequest("Entertainment");

            when(categoryValidator.validate(request)).thenReturn(ValidationResult.success());
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(categoryRepository.existsByUser_IdAndName(1L, "Entertainment")).thenReturn(false);

            Category newCategory = new Category();
            newCategory.setName("Entertainment");
            when(categoryMapper.toEntity(request)).thenReturn(newCategory);
            when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> {
                Category c = inv.getArgument(0);
                c.setId(2L);
                return c;
            });
            when(categoryMapper.toResponse(any(Category.class))).thenReturn(
                    new CategoryResponse(2L, "Entertainment")
            );

            // Act
            CategoryResponse response = categoryService.createCategory(1L, request);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.name()).isEqualTo("Entertainment");
            verify(categoryRepository).save(any(Category.class));
        }

        @Test
        @DisplayName("should fail when category name already exists for user")
        void shouldFailWhenCategoryNameAlreadyExists() {
            // Arrange: Try to create duplicate category
            CategoryCreateRequest request = new CategoryCreateRequest("Groceries");

            when(categoryValidator.validate(request)).thenReturn(ValidationResult.success());
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            // Name already exists
            when(categoryRepository.existsByUser_IdAndName(1L, "Groceries")).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> categoryService.createCategory(1L, request))
                    .isInstanceOf(ValidationException.class);

            verify(categoryRepository, never()).save(any(Category.class));
        }

        @Test
        @DisplayName("should fail when request is null")
        void shouldFailWhenRequestIsNull() {
            // Act & Assert
            assertThatThrownBy(() -> categoryService.createCategory(1L, null))
                    .isInstanceOf(ValidationException.class);
        }

        @Test
        @DisplayName("should fail when user not found")
        void shouldFailWhenUserNotFound() {
            // Arrange
            CategoryCreateRequest request = new CategoryCreateRequest("NewCategory");

            when(categoryValidator.validate(request)).thenReturn(ValidationResult.success());
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> categoryService.createCategory(999L, request))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    // ==================== Update Category Tests ====================

    @Nested
    @DisplayName("Update Category")
    class UpdateCategory {

        @Test
        @DisplayName("should update category name successfully")
        void shouldUpdateCategoryNameSuccessfully() {
            // Arrange
            CategoryUpdateRequest request = new CategoryUpdateRequest("Updated Groceries");

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(categoryRepository.findByUser_IdAndId(1L, 1L)).thenReturn(Optional.of(testCategory));
            when(categoryRepository.existsByUser_IdAndName(1L, "Updated Groceries")).thenReturn(false);
            when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);
            when(categoryMapper.toResponse(any(Category.class))).thenReturn(testCategoryResponse);

            // Act
            CategoryResponse response = categoryService.updateCategory(1L, 1L, request);

            // Assert
            assertThat(response).isNotNull();
            verify(categoryRepository).save(testCategory);
        }

        @Test
        @DisplayName("should allow keeping same name (no-op update)")
        void shouldAllowKeepingSameName() {
            // Arrange: Update with same name - should not trigger duplicate check
            CategoryUpdateRequest request = new CategoryUpdateRequest("Groceries");

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(categoryRepository.findByUser_IdAndId(1L, 1L)).thenReturn(Optional.of(testCategory));
            when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);
            when(categoryMapper.toResponse(any(Category.class))).thenReturn(testCategoryResponse);

            // Act: Should not fail - same name is allowed
            CategoryResponse response = categoryService.updateCategory(1L, 1L, request);

            // Assert
            assertThat(response).isNotNull();
            // Should NOT check for duplicates since name is unchanged
            verify(categoryRepository, never()).existsByUser_IdAndName(anyLong(), anyString());
        }

        @Test
        @DisplayName("should fail when changing to existing name")
        void shouldFailWhenChangingToExistingName() {
            // Arrange: Another category with "Food" already exists
            CategoryUpdateRequest request = new CategoryUpdateRequest("Food");

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(categoryRepository.findByUser_IdAndId(1L, 1L)).thenReturn(Optional.of(testCategory));
            when(categoryRepository.existsByUser_IdAndName(1L, "Food")).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> categoryService.updateCategory(1L, 1L, request))
                    .isInstanceOf(ValidationException.class);
        }

        @Test
        @DisplayName("should fail when category not found")
        void shouldFailWhenCategoryNotFound() {
            // Arrange
            CategoryUpdateRequest request = new CategoryUpdateRequest("New Name");

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(categoryRepository.findByUser_IdAndId(1L, 999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> categoryService.updateCategory(1L, 999L, request))
                    .isInstanceOf(CategoryNotFoundException.class);
        }
    }

    // ==================== Delete Category Tests ====================

    @Nested
    @DisplayName("Delete Category")
    class DeleteCategory {

        @Test
        @DisplayName("should delete category successfully")
        void shouldDeleteCategorySuccessfully() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(categoryRepository.findByUser_IdAndId(1L, 1L)).thenReturn(Optional.of(testCategory));

            // Act
            categoryService.deleteCategory(1L, 1L);

            // Assert
            verify(categoryRepository).delete(testCategory);
        }

        @Test
        @DisplayName("should fail when category not found")
        void shouldFailWhenCategoryNotFound() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(categoryRepository.findByUser_IdAndId(1L, 999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> categoryService.deleteCategory(1L, 999L))
                    .isInstanceOf(CategoryNotFoundException.class);
        }
    }

    // ==================== Get Category Tests ====================

    @Nested
    @DisplayName("Get Category")
    class GetCategory {

        @Test
        @DisplayName("should get single category successfully")
        void shouldGetSingleCategorySuccessfully() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(categoryRepository.findByUser_IdAndId(1L, 1L)).thenReturn(Optional.of(testCategory));
            when(categoryMapper.toResponse(testCategory)).thenReturn(testCategoryResponse);

            // Act
            CategoryResponse response = categoryService.getCategory(1L, 1L);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(1L);
        }

        @Test
        @DisplayName("should get all categories for user")
        void shouldGetAllCategoriesForUser() {
            // Arrange
            Category secondCategory = new Category();
            secondCategory.setId(2L);
            secondCategory.setName("Transportation");
            secondCategory.setUser(testUser);

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(categoryRepository.findByUser_Id(1L)).thenReturn(List.of(testCategory, secondCategory));
            when(categoryMapper.toResponse(any(Category.class))).thenReturn(testCategoryResponse);

            // Act
            List<CategoryResponse> responses = categoryService.getCategories(1L);

            // Assert
            assertThat(responses).hasSize(2);
        }

        @Test
        @DisplayName("should return empty list when user has no categories")
        void shouldReturnEmptyListWhenNoCategoriesExist() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(categoryRepository.findByUser_Id(1L)).thenReturn(List.of());

            // Act
            List<CategoryResponse> responses = categoryService.getCategories(1L);

            // Assert
            assertThat(responses).isEmpty();
        }
    }

    // ==================== Find Or Resolve General Category Tests ====================

    @Nested
    @DisplayName("Find Or Resolve General Category")
    class FindOrResolveGeneralCategory {

        @Test
        @DisplayName("should resolve category ID 0 to General category")
        void shouldResolveCategoryIdZeroToGeneralCategory() {
            // Arrange: Category ID 0 is special - maps to "General"
            when(categoryRepository.findByUser_IdAndName(1L, "General"))
                    .thenReturn(Optional.of(generalCategory));

            // Act
            Category result = categoryService.findOrResolveGeneralCategory(1L, 0L);

            // Assert: Should return the General category
            assertThat(result).isEqualTo(generalCategory);
            assertThat(result.getName()).isEqualTo("General");
        }

        @Test
        @DisplayName("should return specific category when ID is not 0")
        void shouldReturnSpecificCategoryWhenIdIsNotZero() {
            // Arrange: Non-zero category ID
            when(categoryRepository.findByUser_IdAndId(1L, 1L))
                    .thenReturn(Optional.of(testCategory));

            // Act
            Category result = categoryService.findOrResolveGeneralCategory(1L, 1L);

            // Assert: Should return the specific category
            assertThat(result).isEqualTo(testCategory);
            assertThat(result.getName()).isEqualTo("Groceries");
        }

        @Test
        @DisplayName("should throw exception when General category not found")
        void shouldThrowExceptionWhenGeneralCategoryNotFound() {
            // Arrange: General category doesn't exist (shouldn't happen in practice)
            when(categoryRepository.findByUser_IdAndName(1L, "General"))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> categoryService.findOrResolveGeneralCategory(1L, 0L))
                    .isInstanceOf(CategoryNotFoundException.class);
        }

        @Test
        @DisplayName("should throw exception when specific category not found")
        void shouldThrowExceptionWhenSpecificCategoryNotFound() {
            // Arrange
            when(categoryRepository.findByUser_IdAndId(1L, 999L))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> categoryService.findOrResolveGeneralCategory(1L, 999L))
                    .isInstanceOf(CategoryNotFoundException.class);
        }
    }
}
