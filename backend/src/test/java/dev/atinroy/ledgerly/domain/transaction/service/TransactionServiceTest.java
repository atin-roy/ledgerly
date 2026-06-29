package dev.atinroy.ledgerly.domain.transaction.service;

import dev.atinroy.ledgerly.domain.category.service.CategoryService;
import dev.atinroy.ledgerly.domain.party.service.PartyService;
import dev.atinroy.ledgerly.domain.transaction.dto.TransactionCreateRequest;
import dev.atinroy.ledgerly.domain.transaction.dto.TransactionUpdateRequest;
import dev.atinroy.ledgerly.domain.transaction.dto.TransactionResponse;
import dev.atinroy.ledgerly.domain.category.entity.Category;
import dev.atinroy.ledgerly.domain.party.entity.Party;
import dev.atinroy.ledgerly.domain.transaction.entity.Transaction;
import dev.atinroy.ledgerly.domain.user.entity.User;
import dev.atinroy.ledgerly.domain.user.entity.UserRole;
import dev.atinroy.ledgerly.domain.transaction.exception.TransactionNotFoundException;
import dev.atinroy.ledgerly.domain.user.exception.UserNotFoundException;
import dev.atinroy.ledgerly.shared.error.ValidationException;
import dev.atinroy.ledgerly.domain.transaction.mapper.TransactionMapper;
import dev.atinroy.ledgerly.domain.transaction.repository.TransactionRepository;
import dev.atinroy.ledgerly.domain.user.repository.UserRepository;
import dev.atinroy.ledgerly.domain.transaction.validator.TransactionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TransactionService.
 *
 * TransactionService handles CRUD operations for transactions with:
 * - Category ID 0 resolution (maps to "General" category)
 * - Auto-creation of parties from party names
 * - Validation of transaction data
 * - Pagination support for listing
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionService Tests")
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private PartyService partyService;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private TransactionValidator transactionValidator;

    @InjectMocks
    private TransactionService transactionService;

    // Test fixtures
    private User testUser;
    private Category testCategory;
    private Category generalCategory;
    private Party testParty;
    private Transaction testTransaction;
    private TransactionResponse testTransactionResponse;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setRole(UserRole.USER);

        // Create test category
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Food");
        testCategory.setUser(testUser);

        // Create "General" category (ID 0 maps to this)
        generalCategory = new Category();
        generalCategory.setId(100L);  // Real ID in database
        generalCategory.setName("General");
        generalCategory.setUser(testUser);

        // Create test party
        testParty = new Party();
        testParty.setId(1L);
        testParty.setName("Coffee Shop");
        testParty.setNormalizedName("coffee shop");
        testParty.setUser(testUser);

        // Create test transaction
        testTransaction = new Transaction();
        testTransaction.setId(1L);
        testTransaction.setAmount(new BigDecimal("25.50"));
        testTransaction.setDate(LocalDateTime.now().minusDays(1));
        testTransaction.setCategory(testCategory);
        testTransaction.setUser(testUser);
        testTransaction.setParty(testParty);

        testTransactionResponse = new TransactionResponse(
                1L,
                new BigDecimal("25.50"),
                LocalDateTime.now().minusDays(1),
                1L,
                "Food",
                "Coffee Shop",
                null
        );
    }

    // ==================== Create Transaction Tests ====================

    @Nested
    @DisplayName("Create Transaction")
    class CreateTransaction {

        @Test
        @DisplayName("should create transaction successfully with valid data")
        void shouldCreateTransactionSuccessfully() {
            // Arrange
            TransactionCreateRequest request = new TransactionCreateRequest(
                    new BigDecimal("50.00"),
                    LocalDateTime.now().minusHours(1),
                    1L,
                    "Restaurant",
                    null
            );

            // Mock: Validation passes
            when(transactionValidator.validate(request))
                    .thenReturn(dev.atinroy.ledgerly.shared.error.ValidationResult.success());

            // Mock: User exists
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            // Mock: Category resolution
            when(categoryService.findOrResolveGeneralCategory(1L, 1L)).thenReturn(testCategory);

            // Mock: Party creation/lookup
            Party newParty = new Party();
            newParty.setId(2L);
            newParty.setName("Restaurant");
            when(partyService.findOrCreateParty(1L, "Restaurant")).thenReturn(newParty);

            // Mock: Mapper converts request to entity
            Transaction mappedTransaction = new Transaction();
            when(transactionMapper.toEntity(request)).thenReturn(mappedTransaction);

            // Mock: Save
            when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
                Transaction t = invocation.getArgument(0);
                t.setId(1L);
                return t;
            });

            // Mock: Response mapping
            when(transactionMapper.toResponse(any(Transaction.class))).thenReturn(testTransactionResponse);

            // Act
            TransactionResponse response = transactionService.createTransaction(1L, request);

            // Assert
            assertThat(response).isNotNull();
            verify(transactionRepository).save(any(Transaction.class));
            verify(partyService).findOrCreateParty(1L, "Restaurant");
        }

        @Test
        @DisplayName("should create transaction with category ID 0 (General)")
        void shouldCreateTransactionWithCategoryIdZero() {
            // Arrange: Category ID 0 should resolve to "General" category
            TransactionCreateRequest request = new TransactionCreateRequest(
                    new BigDecimal("100.00"),
                    LocalDateTime.now().minusHours(1),
                    0L,  // Category ID 0 = General
                    null,
                    null
            );

            when(transactionValidator.validate(request))
                    .thenReturn(dev.atinroy.ledgerly.shared.error.ValidationResult.success());
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(categoryService.findOrResolveGeneralCategory(1L, 0L)).thenReturn(generalCategory);
            when(transactionMapper.toEntity(request)).thenReturn(new Transaction());
            when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> {
                Transaction t = inv.getArgument(0);
                t.setId(1L);
                return t;
            });
            when(transactionMapper.toResponse(any(Transaction.class))).thenReturn(testTransactionResponse);

            // Act
            TransactionResponse response = transactionService.createTransaction(1L, request);

            // Assert: Verify General category was resolved
            verify(categoryService).findOrResolveGeneralCategory(1L, 0L);
            assertThat(response).isNotNull();
        }

        @Test
        @DisplayName("should create transaction without party (party name is null)")
        void shouldCreateTransactionWithoutParty() {
            // Arrange: No party name provided
            TransactionCreateRequest request = new TransactionCreateRequest(
                    new BigDecimal("75.00"),
                    LocalDateTime.now().minusHours(1),
                    1L,
                    null,  // No party
                    null
            );

            when(transactionValidator.validate(request))
                    .thenReturn(dev.atinroy.ledgerly.shared.error.ValidationResult.success());
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(categoryService.findOrResolveGeneralCategory(1L, 1L)).thenReturn(testCategory);
            when(transactionMapper.toEntity(request)).thenReturn(new Transaction());
            when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> {
                Transaction t = inv.getArgument(0);
                t.setId(1L);
                return t;
            });
            when(transactionMapper.toResponse(any(Transaction.class))).thenReturn(testTransactionResponse);

            // Act
            transactionService.createTransaction(1L, request);

            // Assert: PartyService should NOT be called
            verify(partyService, never()).findOrCreateParty(anyLong(), anyString());
        }

        @Test
        @DisplayName("should fail when request is null")
        void shouldFailWhenRequestIsNull() {
            // Act & Assert
            assertThatThrownBy(() -> transactionService.createTransaction(1L, null))
                    .isInstanceOf(ValidationException.class);
        }

        @Test
        @DisplayName("should fail when user not found")
        void shouldFailWhenUserNotFound() {
            // Arrange
            TransactionCreateRequest request = new TransactionCreateRequest(
                    new BigDecimal("50.00"),
                    LocalDateTime.now().minusHours(1),
                    1L,
                    null,
                    null
            );

            when(transactionValidator.validate(request))
                    .thenReturn(dev.atinroy.ledgerly.shared.error.ValidationResult.success());
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> transactionService.createTransaction(999L, request))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        @DisplayName("should fail when validation fails")
        void shouldFailWhenValidationFails() {
            // Arrange
            TransactionCreateRequest request = new TransactionCreateRequest(
                    BigDecimal.ZERO,  // Invalid: zero amount
                    LocalDateTime.now().plusDays(1),  // Invalid: future date
                    -1L,  // Invalid: negative category
                    null,
                    null
            );

            // Create validation result with errors
            var validationResult = dev.atinroy.ledgerly.shared.error.ValidationResult.withErrors();
            validationResult.addFieldError("amount", "INVALID_VALUE", "Amount cannot be zero");
            when(transactionValidator.validate(request)).thenReturn(validationResult);

            // Act & Assert
            assertThatThrownBy(() -> transactionService.createTransaction(1L, request))
                    .isInstanceOf(ValidationException.class);

            // Verify: Should not attempt to save
            verify(transactionRepository, never()).save(any(Transaction.class));
        }
    }

    // ==================== Update Transaction Tests ====================

    @Nested
    @DisplayName("Update Transaction")
    class UpdateTransaction {

        @Test
        @DisplayName("should update transaction amount successfully")
        void shouldUpdateTransactionAmountSuccessfully() {
            // Arrange
            TransactionUpdateRequest request = new TransactionUpdateRequest(
                    new BigDecimal("100.00"),  // New amount
                    null,  // Keep date
                    null,  // Keep category
                    null,  // Keep party
                    null   // Keep description
            );

            when(transactionValidator.validate(request))
                    .thenReturn(dev.atinroy.ledgerly.shared.error.ValidationResult.success());
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(transactionRepository.findByUser_IdAndId(1L, 1L)).thenReturn(Optional.of(testTransaction));
            when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);
            when(transactionMapper.toResponse(any(Transaction.class))).thenReturn(testTransactionResponse);

            // Act
            TransactionResponse response = transactionService.updateTransaction(1L, 1L, request);

            // Assert
            assertThat(response).isNotNull();
            verify(transactionRepository).save(testTransaction);
        }

        @Test
        @DisplayName("should update party by setting new party name")
        void shouldUpdatePartyBySettingNewPartyName() {
            // Arrange: Update to new party
            TransactionUpdateRequest request = new TransactionUpdateRequest(
                    null,  // Keep amount
                    null,  // Keep date
                    null,  // Keep category
                    "New Restaurant",  // New party name
                    null   // Keep description
            );

            Party newParty = new Party();
            newParty.setId(5L);
            newParty.setName("New Restaurant");

            when(transactionValidator.validate(request))
                    .thenReturn(dev.atinroy.ledgerly.shared.error.ValidationResult.success());
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(transactionRepository.findByUser_IdAndId(1L, 1L)).thenReturn(Optional.of(testTransaction));
            when(partyService.findOrCreateParty(1L, "New Restaurant")).thenReturn(newParty);
            when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);
            when(transactionMapper.toResponse(any(Transaction.class))).thenReturn(testTransactionResponse);

            // Act
            transactionService.updateTransaction(1L, 1L, request);

            // Assert: Should find/create new party
            verify(partyService).findOrCreateParty(1L, "New Restaurant");
        }

        @Test
        @DisplayName("should remove party when empty string provided")
        void shouldRemovePartyWhenEmptyStringProvided() {
            // Arrange: Empty party name means remove association
            TransactionUpdateRequest request = new TransactionUpdateRequest(
                    null,  // Keep amount
                    null,  // Keep date
                    null,  // Keep category
                    "",    // Empty = remove party
                    null   // Keep description
            );

            when(transactionValidator.validate(request))
                    .thenReturn(dev.atinroy.ledgerly.shared.error.ValidationResult.success());
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(transactionRepository.findByUser_IdAndId(1L, 1L)).thenReturn(Optional.of(testTransaction));
            when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);
            when(transactionMapper.toResponse(any(Transaction.class))).thenReturn(testTransactionResponse);

            // Act
            transactionService.updateTransaction(1L, 1L, request);

            // Assert: Party should be set to null (not looking up empty string)
            verify(partyService, never()).findOrCreateParty(anyLong(), anyString());
            assertThat(testTransaction.getParty()).isNull();
        }

        @Test
        @DisplayName("should fail when transaction not found")
        void shouldFailWhenTransactionNotFound() {
            // Arrange
            TransactionUpdateRequest request = new TransactionUpdateRequest(
                    new BigDecimal("100.00"),
                    null,
                    null,
                    null,
                    null
            );

            when(transactionValidator.validate(request))
                    .thenReturn(dev.atinroy.ledgerly.shared.error.ValidationResult.success());
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(transactionRepository.findByUser_IdAndId(1L, 999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> transactionService.updateTransaction(1L, 999L, request))
                    .isInstanceOf(TransactionNotFoundException.class);
        }
    }

    // ==================== Delete Transaction Tests ====================

    @Nested
    @DisplayName("Delete Transaction")
    class DeleteTransaction {

        @Test
        @DisplayName("should delete transaction successfully")
        void shouldDeleteTransactionSuccessfully() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(transactionRepository.findByUser_IdAndId(1L, 1L)).thenReturn(Optional.of(testTransaction));

            // Act
            transactionService.deleteTransaction(1L, 1L);

            // Assert
            verify(transactionRepository).delete(testTransaction);
        }

        @Test
        @DisplayName("should fail when user not found")
        void shouldFailWhenUserNotFound() {
            // Arrange
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> transactionService.deleteTransaction(999L, 1L))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        @DisplayName("should fail when transaction not found")
        void shouldFailWhenTransactionNotFound() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(transactionRepository.findByUser_IdAndId(1L, 999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> transactionService.deleteTransaction(1L, 999L))
                    .isInstanceOf(TransactionNotFoundException.class);
        }
    }

    // ==================== Get Transaction Tests ====================

    @Nested
    @DisplayName("Get Transaction")
    class GetTransaction {

        @Test
        @DisplayName("should get transaction successfully")
        void shouldGetTransactionSuccessfully() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(transactionRepository.findByUser_IdAndId(1L, 1L)).thenReturn(Optional.of(testTransaction));
            when(transactionMapper.toResponse(testTransaction)).thenReturn(testTransactionResponse);

            // Act
            TransactionResponse response = transactionService.getTransaction(1L, 1L);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(1L);
        }

        @Test
        @DisplayName("should fail when transaction not found")
        void shouldFailWhenTransactionNotFound() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(transactionRepository.findByUser_IdAndId(1L, 999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> transactionService.getTransaction(1L, 999L))
                    .isInstanceOf(TransactionNotFoundException.class);
        }
    }

    // ==================== List Transactions (Paginated) Tests ====================

    @Nested
    @DisplayName("Get Transactions (Paginated)")
    class GetTransactionsPaginated {

        @Test
        @DisplayName("should return paginated transactions")
        void shouldReturnPaginatedTransactions() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            List<Transaction> transactions = List.of(testTransaction);
            Page<Transaction> transactionPage = new PageImpl<>(transactions, pageable, 1);

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(transactionRepository.findByUser_Id(1L, pageable)).thenReturn(transactionPage);
            when(transactionMapper.toResponse(testTransaction)).thenReturn(testTransactionResponse);

            // Act
            Page<TransactionResponse> responsePage = transactionService.getTransactions(1L, pageable);

            // Assert
            assertThat(responsePage).isNotNull();
            assertThat(responsePage.getContent()).hasSize(1);
            assertThat(responsePage.getTotalElements()).isEqualTo(1);
        }

        @Test
        @DisplayName("should return empty page when no transactions")
        void shouldReturnEmptyPageWhenNoTransactions() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<Transaction> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(transactionRepository.findByUser_Id(1L, pageable)).thenReturn(emptyPage);

            // Act
            Page<TransactionResponse> responsePage = transactionService.getTransactions(1L, pageable);

            // Assert
            assertThat(responsePage.getContent()).isEmpty();
            assertThat(responsePage.getTotalElements()).isEqualTo(0);
        }

        @Test
        @DisplayName("should fail when user not found")
        void shouldFailWhenUserNotFound() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> transactionService.getTransactions(999L, pageable))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }
}
