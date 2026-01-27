package dev.atinroy.ledgerly.service;

import dev.atinroy.ledgerly.dto.request.BudgetCreateRequest;
import dev.atinroy.ledgerly.dto.request.BudgetUpdateRequest;
import dev.atinroy.ledgerly.dto.response.BudgetResponse;
import dev.atinroy.ledgerly.entity.Budget;
import dev.atinroy.ledgerly.entity.Category;
import dev.atinroy.ledgerly.entity.User;
import dev.atinroy.ledgerly.entity.enums.UserRole;
import dev.atinroy.ledgerly.error.BudgetNotFoundException;
import dev.atinroy.ledgerly.error.UserNotFoundException;
import dev.atinroy.ledgerly.error.ValidationException;
import dev.atinroy.ledgerly.mapper.BudgetMapper;
import dev.atinroy.ledgerly.repository.BudgetRepository;
import dev.atinroy.ledgerly.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BudgetService.
 *
 * BudgetService manages spending budgets with:
 * - One budget per category constraint
 * - Category ID 0 resolution (General category)
 * - Standard CRUD operations
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BudgetService Tests")
class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private BudgetMapper budgetMapper;

    @InjectMocks
    private BudgetService budgetService;

    // Test fixtures
    private User testUser;
    private Category testCategory;
    private Category generalCategory;
    private Budget testBudget;
    private BudgetResponse testBudgetResponse;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setRole(UserRole.USER);

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Food");
        testCategory.setUser(testUser);

        generalCategory = new Category();
        generalCategory.setId(100L);
        generalCategory.setName("General");
        generalCategory.setUser(testUser);

        testBudget = new Budget();
        testBudget.setId(1L);
        testBudget.setAmount(new BigDecimal("500.00"));
        testBudget.setCategory(testCategory);
        testBudget.setUser(testUser);

        // BudgetResponse(id, amount, categoryId)
        testBudgetResponse = new BudgetResponse(1L, new BigDecimal("500.00"), 1L);
    }

    // ==================== Create Budget Tests ====================

    @Nested
    @DisplayName("Create Budget")
    class CreateBudget {

        @Test
        @DisplayName("should create budget successfully")
        void shouldCreateBudgetSuccessfully() {
            // Arrange
            BudgetCreateRequest request = new BudgetCreateRequest(
                    new BigDecimal("300.00"),
                    2L  // Different category
            );

            Category entertainmentCategory = new Category();
            entertainmentCategory.setId(2L);
            entertainmentCategory.setName("Entertainment");

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(categoryService.findOrResolveGeneralCategory(1L, 2L))
                    .thenReturn(entertainmentCategory);
            when(budgetRepository.findByUser_IdAndCategory_Id(1L, 2L))
                    .thenReturn(Optional.empty());  // No existing budget

            Budget newBudget = new Budget();
            when(budgetMapper.toEntity(request)).thenReturn(newBudget);
            when(budgetRepository.save(any(Budget.class))).thenAnswer(inv -> {
                Budget b = inv.getArgument(0);
                b.setId(2L);
                return b;
            });
            when(budgetMapper.toResponse(any(Budget.class))).thenReturn(
                    new BudgetResponse(2L, new BigDecimal("300.00"), 2L)
            );

            // Act
            BudgetResponse response = budgetService.createBudget(1L, request);

            // Assert
            assertThat(response).isNotNull();
            verify(budgetRepository).save(any(Budget.class));
        }

        @Test
        @DisplayName("should create budget for General category using ID 0")
        void shouldCreateBudgetForGeneralCategoryUsingIdZero() {
            // Arrange: Category ID 0 maps to General
            BudgetCreateRequest request = new BudgetCreateRequest(
                    new BigDecimal("200.00"),
                    0L  // General category
            );

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(categoryService.findOrResolveGeneralCategory(1L, 0L))
                    .thenReturn(generalCategory);
            when(budgetRepository.findByUser_IdAndCategory_Id(1L, 100L))  // Real ID
                    .thenReturn(Optional.empty());

            Budget newBudget = new Budget();
            when(budgetMapper.toEntity(request)).thenReturn(newBudget);
            when(budgetRepository.save(any(Budget.class))).thenAnswer(inv -> {
                Budget b = inv.getArgument(0);
                b.setId(3L);
                return b;
            });
            when(budgetMapper.toResponse(any(Budget.class))).thenReturn(testBudgetResponse);

            // Act
            BudgetResponse response = budgetService.createBudget(1L, request);

            // Assert: Category should be resolved via CategoryService
            verify(categoryService).findOrResolveGeneralCategory(1L, 0L);
        }

        @Test
        @DisplayName("should fail when budget already exists for category")
        void shouldFailWhenBudgetAlreadyExistsForCategory() {
            // Arrange: Budget for Food category already exists
            BudgetCreateRequest request = new BudgetCreateRequest(
                    new BigDecimal("600.00"),
                    1L  // Food category
            );

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(categoryService.findOrResolveGeneralCategory(1L, 1L))
                    .thenReturn(testCategory);
            // Budget already exists
            when(budgetRepository.findByUser_IdAndCategory_Id(1L, 1L))
                    .thenReturn(Optional.of(testBudget));

            // Act & Assert: One budget per category
            assertThatThrownBy(() -> budgetService.createBudget(1L, request))
                    .isInstanceOf(ValidationException.class);

            verify(budgetRepository, never()).save(any(Budget.class));
        }

        @Test
        @DisplayName("should fail when request is null")
        void shouldFailWhenRequestIsNull() {
            // Act & Assert
            assertThatThrownBy(() -> budgetService.createBudget(1L, null))
                    .isInstanceOf(ValidationException.class);
        }

        @Test
        @DisplayName("should fail when user not found")
        void shouldFailWhenUserNotFound() {
            // Arrange
            BudgetCreateRequest request = new BudgetCreateRequest(
                    new BigDecimal("500.00"),
                    1L
            );

            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> budgetService.createBudget(999L, request))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    // ==================== Update Budget Tests ====================

    @Nested
    @DisplayName("Update Budget")
    class UpdateBudget {

        @Test
        @DisplayName("should update budget amount successfully")
        void shouldUpdateBudgetAmountSuccessfully() {
            // Arrange
            // BudgetUpdateRequest(id, amount, categoryId)
            BudgetUpdateRequest request = new BudgetUpdateRequest(
                    1L,                         // Budget ID
                    new BigDecimal("750.00"),  // New amount
                    null                        // Keep category
            );

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(budgetRepository.findByUser_IdAndId(1L, 1L)).thenReturn(Optional.of(testBudget));
            when(budgetRepository.save(any(Budget.class))).thenReturn(testBudget);
            when(budgetMapper.toResponse(any(Budget.class))).thenReturn(testBudgetResponse);

            // Act
            BudgetResponse response = budgetService.updateBudget(1L, 1L, request);

            // Assert
            assertThat(response).isNotNull();
            assertThat(testBudget.getAmount()).isEqualTo(new BigDecimal("750.00"));
        }

        @Test
        @DisplayName("should update budget category successfully")
        void shouldUpdateBudgetCategorySuccessfully() {
            // Arrange: Change from Food to Entertainment
            Category entertainmentCategory = new Category();
            entertainmentCategory.setId(2L);
            entertainmentCategory.setName("Entertainment");

            // BudgetUpdateRequest(id, amount, categoryId)
            BudgetUpdateRequest request = new BudgetUpdateRequest(
                    1L,    // Budget ID
                    null,  // Keep amount
                    2L     // New category
            );

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(budgetRepository.findByUser_IdAndId(1L, 1L)).thenReturn(Optional.of(testBudget));
            when(categoryService.findOrResolveGeneralCategory(1L, 2L))
                    .thenReturn(entertainmentCategory);
            // No existing budget for Entertainment
            when(budgetRepository.findByUser_IdAndCategory_Id(1L, 2L))
                    .thenReturn(Optional.empty());
            when(budgetRepository.save(any(Budget.class))).thenReturn(testBudget);
            when(budgetMapper.toResponse(any(Budget.class))).thenReturn(testBudgetResponse);

            // Act
            budgetService.updateBudget(1L, 1L, request);

            // Assert: Category should be updated
            assertThat(testBudget.getCategory()).isEqualTo(entertainmentCategory);
        }

        @Test
        @DisplayName("should allow keeping same category")
        void shouldAllowKeepingSameCategory() {
            // Arrange: Update to same category (no-op for category)
            // BudgetUpdateRequest(id, amount, categoryId)
            BudgetUpdateRequest request = new BudgetUpdateRequest(
                    1L,                        // Budget ID
                    new BigDecimal("600.00"),
                    1L                         // Same category
            );

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(budgetRepository.findByUser_IdAndId(1L, 1L)).thenReturn(Optional.of(testBudget));
            when(categoryService.findOrResolveGeneralCategory(1L, 1L))
                    .thenReturn(testCategory);
            when(budgetRepository.save(any(Budget.class))).thenReturn(testBudget);
            when(budgetMapper.toResponse(any(Budget.class))).thenReturn(testBudgetResponse);

            // Act: Should succeed
            budgetService.updateBudget(1L, 1L, request);

            // Assert: Should NOT check for duplicates since category unchanged
            verify(budgetRepository, never()).findByUser_IdAndCategory_Id(anyLong(), anyLong());
        }

        @Test
        @DisplayName("should fail when changing to category with existing budget")
        void shouldFailWhenChangingToCategoryWithExistingBudget() {
            // Arrange: Entertainment category already has a budget
            Category entertainmentCategory = new Category();
            entertainmentCategory.setId(2L);
            entertainmentCategory.setName("Entertainment");

            Budget existingEntertainmentBudget = new Budget();
            existingEntertainmentBudget.setId(3L);
            existingEntertainmentBudget.setCategory(entertainmentCategory);

            // BudgetUpdateRequest(id, amount, categoryId)
            BudgetUpdateRequest request = new BudgetUpdateRequest(1L, null, 2L);

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(budgetRepository.findByUser_IdAndId(1L, 1L)).thenReturn(Optional.of(testBudget));
            when(categoryService.findOrResolveGeneralCategory(1L, 2L))
                    .thenReturn(entertainmentCategory);
            // Budget already exists for Entertainment
            when(budgetRepository.findByUser_IdAndCategory_Id(1L, 2L))
                    .thenReturn(Optional.of(existingEntertainmentBudget));

            // Act & Assert
            assertThatThrownBy(() -> budgetService.updateBudget(1L, 1L, request))
                    .isInstanceOf(ValidationException.class);

            verify(budgetRepository, never()).save(any(Budget.class));
        }

        @Test
        @DisplayName("should fail when budget not found")
        void shouldFailWhenBudgetNotFound() {
            // Arrange
            // BudgetUpdateRequest(id, amount, categoryId)
            BudgetUpdateRequest request = new BudgetUpdateRequest(
                    999L,                      // Non-existent budget ID
                    new BigDecimal("500.00"),
                    null
            );

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(budgetRepository.findByUser_IdAndId(1L, 999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> budgetService.updateBudget(1L, 999L, request))
                    .isInstanceOf(BudgetNotFoundException.class);
        }
    }

    // ==================== Delete Budget Tests ====================

    @Nested
    @DisplayName("Delete Budget")
    class DeleteBudget {

        @Test
        @DisplayName("should delete budget successfully")
        void shouldDeleteBudgetSuccessfully() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(budgetRepository.findByUser_IdAndId(1L, 1L)).thenReturn(Optional.of(testBudget));

            // Act
            budgetService.deleteBudget(1L, 1L);

            // Assert
            verify(budgetRepository).delete(testBudget);
        }
    }

    // ==================== Get Budgets Tests ====================

    @Nested
    @DisplayName("Get Budgets")
    class GetBudgets {

        @Test
        @DisplayName("should return all budgets for user")
        void shouldReturnAllBudgetsForUser() {
            // Arrange
            Budget secondBudget = new Budget();
            secondBudget.setId(2L);
            secondBudget.setAmount(new BigDecimal("200.00"));
            secondBudget.setUser(testUser);

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(budgetRepository.findByUser_Id(1L)).thenReturn(List.of(testBudget, secondBudget));
            when(budgetMapper.toResponse(any(Budget.class))).thenReturn(testBudgetResponse);

            // Act
            List<BudgetResponse> budgets = budgetService.getBudgets(1L);

            // Assert
            assertThat(budgets).hasSize(2);
        }

        @Test
        @DisplayName("should get single budget by ID")
        void shouldGetSingleBudgetById() {
            // Arrange
            when(budgetRepository.findByUser_IdAndId(1L, 1L)).thenReturn(Optional.of(testBudget));
            when(budgetMapper.toResponse(testBudget)).thenReturn(testBudgetResponse);

            // Act
            BudgetResponse budget = budgetService.getBudget(1L, 1L);

            // Assert
            assertThat(budget).isNotNull();
            assertThat(budget.amount()).isEqualTo(new BigDecimal("500.00"));
        }

        @Test
        @DisplayName("should throw exception when budget not found")
        void shouldThrowExceptionWhenBudgetNotFound() {
            // Arrange
            when(budgetRepository.findByUser_IdAndId(1L, 999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> budgetService.getBudget(1L, 999L))
                    .isInstanceOf(BudgetNotFoundException.class);
        }
    }
}
