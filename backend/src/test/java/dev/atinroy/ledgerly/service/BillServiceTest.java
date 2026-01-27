package dev.atinroy.ledgerly.service;

import dev.atinroy.ledgerly.dto.request.BillCreateRequest;
import dev.atinroy.ledgerly.dto.request.BillUpdateRequest;
import dev.atinroy.ledgerly.dto.response.BillResponse;
import dev.atinroy.ledgerly.entity.Bill;
import dev.atinroy.ledgerly.entity.User;
import dev.atinroy.ledgerly.entity.enums.BillStatus;
import dev.atinroy.ledgerly.entity.enums.UserRole;
import dev.atinroy.ledgerly.error.BillNotFoundException;
import dev.atinroy.ledgerly.error.UserNotFoundException;
import dev.atinroy.ledgerly.error.ValidationException;
import dev.atinroy.ledgerly.mapper.BillMapper;
import dev.atinroy.ledgerly.repository.BillRepository;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BillService.
 *
 * BillService manages recurring bills/payments with:
 * - Positive amount validation
 * - Status tracking (PENDING, PAID, OVERDUE, CANCELLED)
 * - Aggregation queries (count and sum by status)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BillService Tests")
class BillServiceTest {

    @Mock
    private BillRepository billRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BillMapper billMapper;

    @InjectMocks
    private BillService billService;

    // Test fixtures
    private User testUser;
    private Bill testBill;
    private BillResponse testBillResponse;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setRole(UserRole.USER);

        testBill = new Bill();
        testBill.setId(1L);
        testBill.setName("Netflix");
        testBill.setAmount(new BigDecimal("15.99"));
        testBill.setDueDate(LocalDateTime.now().plusDays(7));
        testBill.setStatus(BillStatus.PENDING);
        testBill.setUser(testUser);

        // BillResponse(id, name, amount, status, dueDate)
        testBillResponse = new BillResponse(
                1L,
                "Netflix",
                new BigDecimal("15.99"),
                BillStatus.PENDING.name(),
                LocalDateTime.now().plusDays(7)
        );
    }

    // ==================== Create Bill Tests ====================

    @Nested
    @DisplayName("Create Bill")
    class CreateBill {

        @Test
        @DisplayName("should create bill successfully with positive amount")
        void shouldCreateBillSuccessfully() {
            // Arrange
            BillCreateRequest request = new BillCreateRequest(
                    "Spotify",
                    new BigDecimal("9.99"),
                    LocalDateTime.now().plusDays(14),
                    BillStatus.PENDING
            );

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            Bill newBill = new Bill();
            newBill.setName("Spotify");
            when(billMapper.toEntity(request)).thenReturn(newBill);
            when(billRepository.save(any(Bill.class))).thenAnswer(inv -> {
                Bill b = inv.getArgument(0);
                b.setId(2L);
                return b;
            });
            when(billMapper.toResponse(any(Bill.class))).thenReturn(
                    new BillResponse(2L, "Spotify", new BigDecimal("9.99"),
                            BillStatus.PENDING.name(), LocalDateTime.now().plusDays(14))
            );

            // Act
            BillResponse response = billService.createBill(1L, request);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.name()).isEqualTo("Spotify");
            verify(billRepository).save(any(Bill.class));
        }

        @Test
        @DisplayName("should fail when amount is zero")
        void shouldFailWhenAmountIsZero() {
            // Arrange: Zero amount not allowed for bills
            BillCreateRequest request = new BillCreateRequest(
                    "Invalid Bill",
                    BigDecimal.ZERO,  // Invalid
                    LocalDateTime.now().plusDays(7),
                    BillStatus.PENDING
            );

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            // Act & Assert
            assertThatThrownBy(() -> billService.createBill(1L, request))
                    .isInstanceOf(ValidationException.class);

            verify(billRepository, never()).save(any(Bill.class));
        }

        @Test
        @DisplayName("should fail when amount is negative")
        void shouldFailWhenAmountIsNegative() {
            // Arrange: Negative amount not allowed
            BillCreateRequest request = new BillCreateRequest(
                    "Invalid Bill",
                    new BigDecimal("-10.00"),  // Invalid
                    LocalDateTime.now().plusDays(7),
                    BillStatus.PENDING
            );

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            // Act & Assert
            assertThatThrownBy(() -> billService.createBill(1L, request))
                    .isInstanceOf(ValidationException.class);
        }

        @Test
        @DisplayName("should fail when request is null")
        void shouldFailWhenRequestIsNull() {
            // Act & Assert
            assertThatThrownBy(() -> billService.createBill(1L, null))
                    .isInstanceOf(ValidationException.class);
        }

        @Test
        @DisplayName("should fail when user not found")
        void shouldFailWhenUserNotFound() {
            // Arrange
            BillCreateRequest request = new BillCreateRequest(
                    "Some Bill",
                    new BigDecimal("25.00"),
                    LocalDateTime.now().plusDays(7),
                    BillStatus.PENDING
            );

            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> billService.createBill(999L, request))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    // ==================== Update Bill Tests ====================

    @Nested
    @DisplayName("Update Bill")
    class UpdateBill {

        @Test
        @DisplayName("should update bill status successfully")
        void shouldUpdateBillStatusSuccessfully() {
            // Arrange: Mark bill as paid
            // BillUpdateRequest(id, name, amount, dueDate, status)
            BillUpdateRequest request = new BillUpdateRequest(
                    1L,    // Bill ID
                    null,  // Keep name
                    null,  // Keep amount
                    null,  // Keep due date
                    BillStatus.PAID  // Update status
            );

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(billRepository.findByUser_IdAndId(1L, 1L)).thenReturn(Optional.of(testBill));
            when(billRepository.save(any(Bill.class))).thenReturn(testBill);
            when(billMapper.toResponse(any(Bill.class))).thenReturn(testBillResponse);

            // Act
            BillResponse response = billService.updateBill(1L, 1L, request);

            // Assert
            assertThat(response).isNotNull();
            assertThat(testBill.getStatus()).isEqualTo(BillStatus.PAID);
        }

        @Test
        @DisplayName("should update bill amount with positive value")
        void shouldUpdateBillAmountWithPositiveValue() {
            // Arrange
            // BillUpdateRequest(id, name, amount, dueDate, status)
            BillUpdateRequest request = new BillUpdateRequest(
                    1L,
                    null,
                    new BigDecimal("19.99"),  // New amount
                    null,
                    null
            );

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(billRepository.findByUser_IdAndId(1L, 1L)).thenReturn(Optional.of(testBill));
            when(billRepository.save(any(Bill.class))).thenReturn(testBill);
            when(billMapper.toResponse(any(Bill.class))).thenReturn(testBillResponse);

            // Act
            billService.updateBill(1L, 1L, request);

            // Assert
            assertThat(testBill.getAmount()).isEqualTo(new BigDecimal("19.99"));
        }

        @Test
        @DisplayName("should fail update when amount is not positive")
        void shouldFailUpdateWhenAmountIsNotPositive() {
            // Arrange
            // BillUpdateRequest(id, name, amount, dueDate, status)
            BillUpdateRequest request = new BillUpdateRequest(
                    1L,
                    null,
                    new BigDecimal("-5.00"),  // Invalid
                    null,
                    null
            );

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(billRepository.findByUser_IdAndId(1L, 1L)).thenReturn(Optional.of(testBill));

            // Act & Assert
            assertThatThrownBy(() -> billService.updateBill(1L, 1L, request))
                    .isInstanceOf(ValidationException.class);

            verify(billRepository, never()).save(any(Bill.class));
        }

        @Test
        @DisplayName("should update bill name with trimmed value")
        void shouldUpdateBillNameWithTrimmedValue() {
            // Arrange
            // BillUpdateRequest(id, name, amount, dueDate, status)
            BillUpdateRequest request = new BillUpdateRequest(
                    1L,
                    "  New Name  ",  // Should be trimmed
                    null,
                    null,
                    null
            );

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(billRepository.findByUser_IdAndId(1L, 1L)).thenReturn(Optional.of(testBill));
            when(billRepository.save(any(Bill.class))).thenReturn(testBill);
            when(billMapper.toResponse(any(Bill.class))).thenReturn(testBillResponse);

            // Act
            billService.updateBill(1L, 1L, request);

            // Assert: Name should be trimmed
            assertThat(testBill.getName()).isEqualTo("New Name");
        }

        @Test
        @DisplayName("should fail when bill not found")
        void shouldFailWhenBillNotFound() {
            // Arrange
            // BillUpdateRequest(id, name, amount, dueDate, status)
            BillUpdateRequest request = new BillUpdateRequest(1L, null, null, null, BillStatus.PAID);

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(billRepository.findByUser_IdAndId(1L, 999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> billService.updateBill(1L, 999L, request))
                    .isInstanceOf(BillNotFoundException.class);
        }
    }

    // ==================== Delete Bill Tests ====================

    @Nested
    @DisplayName("Delete Bill")
    class DeleteBill {

        @Test
        @DisplayName("should delete bill successfully")
        void shouldDeleteBillSuccessfully() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(billRepository.findByUser_IdAndId(1L, 1L)).thenReturn(Optional.of(testBill));

            // Act
            billService.deleteBill(1L, 1L);

            // Assert
            verify(billRepository).delete(testBill);
        }
    }

    // ==================== Get Bills Tests ====================

    @Nested
    @DisplayName("Get Bills")
    class GetBills {

        @Test
        @DisplayName("should return all bills for user")
        void shouldReturnAllBillsForUser() {
            // Arrange
            Bill secondBill = new Bill();
            secondBill.setId(2L);
            secondBill.setName("Gym");
            secondBill.setAmount(new BigDecimal("50.00"));
            secondBill.setUser(testUser);

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(billRepository.findByUser_Id(1L)).thenReturn(List.of(testBill, secondBill));
            when(billMapper.toResponse(any(Bill.class))).thenReturn(testBillResponse);

            // Act
            List<BillResponse> bills = billService.getBills(1L);

            // Assert
            assertThat(bills).hasSize(2);
        }

        @Test
        @DisplayName("should get single bill by ID")
        void shouldGetSingleBillById() {
            // Arrange
            when(billRepository.findByUser_IdAndId(1L, 1L)).thenReturn(Optional.of(testBill));
            when(billMapper.toResponse(testBill)).thenReturn(testBillResponse);

            // Act
            BillResponse bill = billService.getBill(1L, 1L);

            // Assert
            assertThat(bill).isNotNull();
            assertThat(bill.name()).isEqualTo("Netflix");
        }
    }

    // ==================== Aggregation Tests ====================

    @Nested
    @DisplayName("Bill Aggregations")
    class BillAggregations {

        @Test
        @DisplayName("should count bills by status")
        void shouldCountBillsByStatus() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(billRepository.countByUser_IdAndStatus(1L, BillStatus.PENDING))
                    .thenReturn(5L);

            // Act
            long count = billService.countBillsByStatus(1L, BillStatus.PENDING);

            // Assert
            assertThat(count).isEqualTo(5);
        }

        @Test
        @DisplayName("should return zero count when no bills with status")
        void shouldReturnZeroCountWhenNoBillsWithStatus() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(billRepository.countByUser_IdAndStatus(1L, BillStatus.OVERDUE))
                    .thenReturn(0L);

            // Act
            long count = billService.countBillsByStatus(1L, BillStatus.OVERDUE);

            // Assert
            assertThat(count).isEqualTo(0);
        }

        @Test
        @DisplayName("should sum bill amounts by status")
        void shouldSumBillAmountsByStatus() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(billRepository.sumAmountByUserIdAndStatus(1L, BillStatus.PENDING))
                    .thenReturn(new BigDecimal("150.00"));

            // Act
            BigDecimal sum = billService.sumBillsByStatus(1L, BillStatus.PENDING);

            // Assert
            assertThat(sum).isEqualTo(new BigDecimal("150.00"));
        }

        @Test
        @DisplayName("should return zero sum when no bills with status")
        void shouldReturnZeroSumWhenNoBillsWithStatus() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(billRepository.sumAmountByUserIdAndStatus(1L, BillStatus.CANCELLED))
                    .thenReturn(null);  // No bills = null from query

            // Act
            BigDecimal sum = billService.sumBillsByStatus(1L, BillStatus.CANCELLED);

            // Assert: Should return ZERO, not null
            assertThat(sum).isEqualTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("should fail aggregations when user not found")
        void shouldFailAggregationsWhenUserNotFound() {
            // Arrange
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> billService.countBillsByStatus(999L, BillStatus.PENDING))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }
}
