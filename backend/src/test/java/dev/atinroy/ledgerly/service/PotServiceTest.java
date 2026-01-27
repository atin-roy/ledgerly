package dev.atinroy.ledgerly.service;

import dev.atinroy.ledgerly.dto.request.PotCreateRequest;
import dev.atinroy.ledgerly.dto.request.PotUpdateRequest;
import dev.atinroy.ledgerly.dto.response.PotResponse;
import dev.atinroy.ledgerly.entity.Pot;
import dev.atinroy.ledgerly.entity.User;
import dev.atinroy.ledgerly.entity.enums.UserRole;
import dev.atinroy.ledgerly.error.PotNotFoundException;
import dev.atinroy.ledgerly.error.UserNotFoundException;
import dev.atinroy.ledgerly.error.ValidationException;
import dev.atinroy.ledgerly.mapper.PotMapper;
import dev.atinroy.ledgerly.repository.PotRepository;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PotService.
 *
 * PotService manages savings pots/goals with:
 * - Unique names per user
 * - Target and saved amount tracking
 * - Standard CRUD operations
 *
 * A "pot" is a savings container where users can track
 * progress toward specific financial goals (e.g., "Vacation Fund").
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PotService Tests")
class PotServiceTest {

    @Mock
    private PotRepository potRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PotMapper potMapper;

    @InjectMocks
    private PotService potService;

    // Test fixtures
    private User testUser;
    private Pot testPot;
    private PotResponse testPotResponse;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setRole(UserRole.USER);

        testPot = new Pot();
        testPot.setId(1L);
        testPot.setName("Vacation Fund");
        testPot.setTarget(new BigDecimal("5000.00"));
        testPot.setSaved(new BigDecimal("1500.00"));
        testPot.setUser(testUser);

        // PotResponse(id, name, target, saved)
        testPotResponse = new PotResponse(
                1L,
                "Vacation Fund",
                new BigDecimal("5000.00"),
                new BigDecimal("1500.00")
        );
    }

    // ==================== Create Pot Tests ====================

    @Nested
    @DisplayName("Create Pot")
    class CreatePot {

        @Test
        @DisplayName("should create pot successfully")
        void shouldCreatePotSuccessfully() {
            // Arrange
            PotCreateRequest request = new PotCreateRequest(
                    "Emergency Fund",
                    new BigDecimal("10000.00"),
                    BigDecimal.ZERO
            );

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(potRepository.existsByUser_IdAndName(1L, "Emergency Fund")).thenReturn(false);

            Pot newPot = new Pot();
            newPot.setName("Emergency Fund");
            when(potMapper.toEntity(request)).thenReturn(newPot);
            when(potRepository.save(any(Pot.class))).thenAnswer(inv -> {
                Pot p = inv.getArgument(0);
                p.setId(2L);
                return p;
            });
            when(potMapper.toResponse(any(Pot.class))).thenReturn(
                    new PotResponse(2L, "Emergency Fund", new BigDecimal("10000.00"), BigDecimal.ZERO)
            );

            // Act
            PotResponse response = potService.createPot(1L, request);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.name()).isEqualTo("Emergency Fund");
            verify(potRepository).save(any(Pot.class));
        }

        @Test
        @DisplayName("should fail when pot name already exists for user")
        void shouldFailWhenPotNameAlreadyExists() {
            // Arrange: Pot with same name exists
            PotCreateRequest request = new PotCreateRequest(
                    "Vacation Fund",  // Already exists
                    new BigDecimal("3000.00"),
                    BigDecimal.ZERO
            );

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(potRepository.existsByUser_IdAndName(1L, "Vacation Fund")).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> potService.createPot(1L, request))
                    .isInstanceOf(ValidationException.class);

            verify(potRepository, never()).save(any(Pot.class));
        }

        @Test
        @DisplayName("should fail when request is null")
        void shouldFailWhenRequestIsNull() {
            // Act & Assert
            assertThatThrownBy(() -> potService.createPot(1L, null))
                    .isInstanceOf(ValidationException.class);
        }

        @Test
        @DisplayName("should fail when user not found")
        void shouldFailWhenUserNotFound() {
            // Arrange
            PotCreateRequest request = new PotCreateRequest(
                    "New Pot",
                    new BigDecimal("1000.00"),
                    BigDecimal.ZERO
            );

            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> potService.createPot(999L, request))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        @DisplayName("should create pot with initial saved amount")
        void shouldCreatePotWithInitialSavedAmount() {
            // Arrange: Create pot with some money already saved
            PotCreateRequest request = new PotCreateRequest(
                    "Car Fund",
                    new BigDecimal("20000.00"),
                    new BigDecimal("5000.00")  // Initial saved
            );

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(potRepository.existsByUser_IdAndName(1L, "Car Fund")).thenReturn(false);

            Pot newPot = new Pot();
            when(potMapper.toEntity(request)).thenReturn(newPot);
            when(potRepository.save(any(Pot.class))).thenReturn(newPot);
            when(potMapper.toResponse(any(Pot.class))).thenReturn(testPotResponse);

            // Act
            PotResponse response = potService.createPot(1L, request);

            // Assert
            assertThat(response).isNotNull();
        }
    }

    // ==================== Update Pot Tests ====================

    @Nested
    @DisplayName("Update Pot")
    class UpdatePot {

        @Test
        @DisplayName("should update pot saved amount successfully")
        void shouldUpdatePotSavedAmountSuccessfully() {
            // Arrange: Add more to savings
            // PotUpdateRequest(id, name, target, saved)
            PotUpdateRequest request = new PotUpdateRequest(
                    1L,    // Pot ID
                    null,  // Keep name
                    null,  // Keep target
                    new BigDecimal("2000.00")  // New saved amount
            );

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(potRepository.findByUser_IdAndId(1L, 1L)).thenReturn(Optional.of(testPot));
            when(potRepository.save(any(Pot.class))).thenReturn(testPot);
            when(potMapper.toResponse(any(Pot.class))).thenReturn(testPotResponse);

            // Act
            PotResponse response = potService.updatePot(1L, 1L, request);

            // Assert
            assertThat(response).isNotNull();
            assertThat(testPot.getSaved()).isEqualTo(new BigDecimal("2000.00"));
        }

        @Test
        @DisplayName("should update pot target successfully")
        void shouldUpdatePotTargetSuccessfully() {
            // Arrange: Increase savings goal
            // PotUpdateRequest(id, name, target, saved)
            PotUpdateRequest request = new PotUpdateRequest(
                    1L,
                    null,
                    new BigDecimal("7500.00"),  // New target
                    null
            );

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(potRepository.findByUser_IdAndId(1L, 1L)).thenReturn(Optional.of(testPot));
            when(potRepository.save(any(Pot.class))).thenReturn(testPot);
            when(potMapper.toResponse(any(Pot.class))).thenReturn(testPotResponse);

            // Act
            potService.updatePot(1L, 1L, request);

            // Assert
            assertThat(testPot.getTarget()).isEqualTo(new BigDecimal("7500.00"));
        }

        @Test
        @DisplayName("should update pot name successfully")
        void shouldUpdatePotNameSuccessfully() {
            // Arrange
            // PotUpdateRequest(id, name, target, saved)
            PotUpdateRequest request = new PotUpdateRequest(
                    1L,
                    "Hawaii Trip",  // New name
                    null,
                    null
            );

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(potRepository.findByUser_IdAndId(1L, 1L)).thenReturn(Optional.of(testPot));
            when(potRepository.existsByUser_IdAndName(1L, "Hawaii Trip")).thenReturn(false);
            when(potRepository.save(any(Pot.class))).thenReturn(testPot);
            when(potMapper.toResponse(any(Pot.class))).thenReturn(testPotResponse);

            // Act
            potService.updatePot(1L, 1L, request);

            // Assert
            assertThat(testPot.getName()).isEqualTo("Hawaii Trip");
        }

        @Test
        @DisplayName("should allow keeping same name")
        void shouldAllowKeepingSameName() {
            // Arrange: Update with same name
            // PotUpdateRequest(id, name, target, saved)
            PotUpdateRequest request = new PotUpdateRequest(
                    1L,
                    "Vacation Fund",  // Same name
                    new BigDecimal("6000.00"),
                    null
            );

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(potRepository.findByUser_IdAndId(1L, 1L)).thenReturn(Optional.of(testPot));
            when(potRepository.save(any(Pot.class))).thenReturn(testPot);
            when(potMapper.toResponse(any(Pot.class))).thenReturn(testPotResponse);

            // Act: Should succeed without checking duplicates
            potService.updatePot(1L, 1L, request);

            // Assert: Should NOT check for duplicates
            verify(potRepository, never()).existsByUser_IdAndName(anyLong(), anyString());
        }

        @Test
        @DisplayName("should fail when changing to existing name")
        void shouldFailWhenChangingToExistingName() {
            // Arrange: Another pot with "Emergency Fund" exists
            // PotUpdateRequest(id, name, target, saved)
            PotUpdateRequest request = new PotUpdateRequest(
                    1L,
                    "Emergency Fund",  // Duplicate name
                    null,
                    null
            );

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(potRepository.findByUser_IdAndId(1L, 1L)).thenReturn(Optional.of(testPot));
            when(potRepository.existsByUser_IdAndName(1L, "Emergency Fund")).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> potService.updatePot(1L, 1L, request))
                    .isInstanceOf(ValidationException.class);

            verify(potRepository, never()).save(any(Pot.class));
        }

        @Test
        @DisplayName("should fail when pot not found")
        void shouldFailWhenPotNotFound() {
            // Arrange
            // PotUpdateRequest(id, name, target, saved)
            PotUpdateRequest request = new PotUpdateRequest(
                    999L,  // Non-existent pot ID
                    null,
                    new BigDecimal("1000.00"),
                    null
            );

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(potRepository.findByUser_IdAndId(1L, 999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> potService.updatePot(1L, 999L, request))
                    .isInstanceOf(PotNotFoundException.class);
        }

        @Test
        @DisplayName("should fail when request is null")
        void shouldFailWhenRequestIsNull() {
            // Act & Assert
            assertThatThrownBy(() -> potService.updatePot(1L, 1L, null))
                    .isInstanceOf(ValidationException.class);
        }
    }

    // ==================== Delete Pot Tests ====================

    @Nested
    @DisplayName("Delete Pot")
    class DeletePot {

        @Test
        @DisplayName("should delete pot successfully")
        void shouldDeletePotSuccessfully() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(potRepository.findByUser_IdAndId(1L, 1L)).thenReturn(Optional.of(testPot));

            // Act
            potService.deletePot(1L, 1L);

            // Assert
            verify(potRepository).delete(testPot);
        }

        @Test
        @DisplayName("should fail when user not found")
        void shouldFailWhenUserNotFound() {
            // Arrange
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> potService.deletePot(999L, 1L))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        @DisplayName("should fail when pot not found")
        void shouldFailWhenPotNotFound() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(potRepository.findByUser_IdAndId(1L, 999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> potService.deletePot(1L, 999L))
                    .isInstanceOf(PotNotFoundException.class);
        }
    }

    // ==================== Get Pots Tests ====================

    @Nested
    @DisplayName("Get Pots")
    class GetPots {

        @Test
        @DisplayName("should return all pots for user")
        void shouldReturnAllPotsForUser() {
            // Arrange
            Pot secondPot = new Pot();
            secondPot.setId(2L);
            secondPot.setName("Emergency Fund");
            secondPot.setTarget(new BigDecimal("10000.00"));
            secondPot.setSaved(new BigDecimal("2500.00"));
            secondPot.setUser(testUser);

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(potRepository.findByUser_Id(1L)).thenReturn(List.of(testPot, secondPot));
            when(potMapper.toResponse(any(Pot.class))).thenReturn(testPotResponse);

            // Act
            List<PotResponse> pots = potService.getPots(1L);

            // Assert
            assertThat(pots).hasSize(2);
        }

        @Test
        @DisplayName("should return empty list when no pots exist")
        void shouldReturnEmptyListWhenNoPotsExist() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(potRepository.findByUser_Id(1L)).thenReturn(List.of());

            // Act
            List<PotResponse> pots = potService.getPots(1L);

            // Assert
            assertThat(pots).isEmpty();
        }

        @Test
        @DisplayName("should get single pot by ID")
        void shouldGetSinglePotById() {
            // Arrange
            when(potRepository.findByUser_IdAndId(1L, 1L)).thenReturn(Optional.of(testPot));
            when(potMapper.toResponse(testPot)).thenReturn(testPotResponse);

            // Act
            PotResponse pot = potService.getPot(1L, 1L);

            // Assert
            assertThat(pot).isNotNull();
            assertThat(pot.name()).isEqualTo("Vacation Fund");
            assertThat(pot.target()).isEqualTo(new BigDecimal("5000.00"));
            assertThat(pot.saved()).isEqualTo(new BigDecimal("1500.00"));
        }

        @Test
        @DisplayName("should throw exception when pot not found")
        void shouldThrowExceptionWhenPotNotFound() {
            // Arrange
            when(potRepository.findByUser_IdAndId(1L, 999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> potService.getPot(1L, 999L))
                    .isInstanceOf(PotNotFoundException.class);
        }

        @Test
        @DisplayName("should fail to get pots when user not found")
        void shouldFailToGetPotsWhenUserNotFound() {
            // Arrange
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> potService.getPots(999L))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }
}
