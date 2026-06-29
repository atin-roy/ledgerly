package dev.atinroy.ledgerly.domain.party.service;

import dev.atinroy.ledgerly.domain.party.dto.PartyCreateRequest;
import dev.atinroy.ledgerly.domain.party.dto.PartyUpdateRequest;
import dev.atinroy.ledgerly.domain.party.dto.PartyResponse;
import dev.atinroy.ledgerly.domain.party.entity.Party;
import dev.atinroy.ledgerly.domain.user.entity.User;
import dev.atinroy.ledgerly.domain.user.entity.UserRole;
import dev.atinroy.ledgerly.domain.party.exception.PartyNotFoundException;
import dev.atinroy.ledgerly.domain.user.exception.UserNotFoundException;
import dev.atinroy.ledgerly.shared.error.ValidationException;
import dev.atinroy.ledgerly.domain.party.mapper.PartyMapper;
import dev.atinroy.ledgerly.domain.party.repository.PartyRepository;
import dev.atinroy.ledgerly.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PartyService.
 *
 * PartyService manages transaction counterparties (merchants, payees) with:
 * - Case-insensitive deduplication via normalized names
 * - Autocomplete search functionality
 * - Find-or-create pattern for transaction creation
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PartyService Tests")
class PartyServiceTest {

    @Mock
    private PartyRepository partyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PartyMapper partyMapper;

    @InjectMocks
    private PartyService partyService;

    // Test fixtures
    private User testUser;
    private Party testParty;
    private PartyResponse testPartyResponse;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setRole(UserRole.USER);

        testParty = new Party();
        testParty.setId(1L);
        testParty.setName("Amazon");
        testParty.setNormalizedName("amazon");
        testParty.setUser(testUser);

        // PartyResponse(id, name)
        testPartyResponse = new PartyResponse(1L, "Amazon");
    }

    // ==================== Create Party Tests ====================

    @Nested
    @DisplayName("Create Party")
    class CreateParty {

        @Test
        @DisplayName("should create party with normalized name")
        void shouldCreatePartyWithNormalizedName() {
            // Arrange: Creating "WALMART" should normalize to "walmart"
            PartyCreateRequest request = new PartyCreateRequest("WALMART");

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            // No existing party with normalized name
            when(partyRepository.findByUser_IdAndNormalizedName(1L, "walmart"))
                    .thenReturn(Optional.empty());

            Party newParty = new Party();
            when(partyMapper.toEntity(request)).thenReturn(newParty);
            when(partyRepository.save(any(Party.class))).thenAnswer(inv -> {
                Party p = inv.getArgument(0);
                p.setId(2L);
                return p;
            });
            when(partyMapper.toResponse(any(Party.class))).thenReturn(
                    new PartyResponse(2L, "WALMART")
            );

            // Act
            PartyResponse response = partyService.createParty(1L, request);

            // Assert: Verify party was saved with normalized name
            ArgumentCaptor<Party> partyCaptor = ArgumentCaptor.forClass(Party.class);
            verify(partyRepository).save(partyCaptor.capture());
            assertThat(partyCaptor.getValue().getNormalizedName()).isEqualTo("walmart");
        }

        @Test
        @DisplayName("should fail when party with same normalized name exists")
        void shouldFailWhenDuplicateNormalizedNameExists() {
            // Arrange: "amazon" already exists, trying to create "AMAZON"
            PartyCreateRequest request = new PartyCreateRequest("AMAZON");

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            // Normalized "amazon" already exists
            when(partyRepository.findByUser_IdAndNormalizedName(1L, "amazon"))
                    .thenReturn(Optional.of(testParty));

            // Act & Assert: Should fail - case-insensitive duplicate
            assertThatThrownBy(() -> partyService.createParty(1L, request))
                    .isInstanceOf(ValidationException.class);

            verify(partyRepository, never()).save(any(Party.class));
        }

        @Test
        @DisplayName("should fail when request is null")
        void shouldFailWhenRequestIsNull() {
            // Act & Assert
            assertThatThrownBy(() -> partyService.createParty(1L, null))
                    .isInstanceOf(ValidationException.class);
        }

        @Test
        @DisplayName("should fail when user not found")
        void shouldFailWhenUserNotFound() {
            // Arrange
            PartyCreateRequest request = new PartyCreateRequest("NewParty");

            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> partyService.createParty(999L, request))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    // ==================== Update Party Tests ====================

    @Nested
    @DisplayName("Update Party")
    class UpdateParty {

        @Test
        @DisplayName("should update party name and normalized name")
        void shouldUpdatePartyNameAndNormalizedName() {
            // Arrange
            // PartyUpdateRequest(id, name)
            PartyUpdateRequest request = new PartyUpdateRequest(1L, "New Name");

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(partyRepository.findByUser_IdAndId(1L, 1L)).thenReturn(Optional.of(testParty));
            when(partyRepository.findByUser_IdAndNormalizedName(1L, "new name"))
                    .thenReturn(Optional.empty());
            when(partyRepository.save(any(Party.class))).thenReturn(testParty);
            when(partyMapper.toResponse(any(Party.class))).thenReturn(testPartyResponse);

            // Act
            partyService.updateParty(1L, 1L, request);

            // Assert: Both name and normalized name should be updated
            assertThat(testParty.getName()).isEqualTo("New Name");
            assertThat(testParty.getNormalizedName()).isEqualTo("new name");
        }

        @Test
        @DisplayName("should allow keeping same normalized name (case change only)")
        void shouldAllowKeepingSameNormalizedName() {
            // Arrange: Change "Amazon" to "AMAZON" (same normalized)
            // PartyUpdateRequest(id, name)
            PartyUpdateRequest request = new PartyUpdateRequest(1L, "AMAZON");

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(partyRepository.findByUser_IdAndId(1L, 1L)).thenReturn(Optional.of(testParty));
            when(partyRepository.save(any(Party.class))).thenReturn(testParty);
            when(partyMapper.toResponse(any(Party.class))).thenReturn(testPartyResponse);

            // Act: Should succeed - normalized name unchanged
            partyService.updateParty(1L, 1L, request);

            // Assert: Name updated but no duplicate check needed
            verify(partyRepository, never()).findByUser_IdAndNormalizedName(anyLong(), anyString());
        }

        @Test
        @DisplayName("should fail when changing to existing normalized name")
        void shouldFailWhenChangingToExistingNormalizedName() {
            // Arrange: "walmart" already exists
            Party existingWalmart = new Party();
            existingWalmart.setId(2L);
            existingWalmart.setNormalizedName("walmart");

            // PartyUpdateRequest(id, name)
            PartyUpdateRequest request = new PartyUpdateRequest(1L, "Walmart");

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(partyRepository.findByUser_IdAndId(1L, 1L)).thenReturn(Optional.of(testParty));
            when(partyRepository.findByUser_IdAndNormalizedName(1L, "walmart"))
                    .thenReturn(Optional.of(existingWalmart));

            // Act & Assert
            assertThatThrownBy(() -> partyService.updateParty(1L, 1L, request))
                    .isInstanceOf(ValidationException.class);
        }

        @Test
        @DisplayName("should fail when party not found")
        void shouldFailWhenPartyNotFound() {
            // Arrange
            // PartyUpdateRequest(id, name)
            PartyUpdateRequest request = new PartyUpdateRequest(1L, "New Name");

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(partyRepository.findByUser_IdAndId(1L, 999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> partyService.updateParty(1L, 999L, request))
                    .isInstanceOf(PartyNotFoundException.class);
        }
    }

    // ==================== Delete Party Tests ====================

    @Nested
    @DisplayName("Delete Party")
    class DeleteParty {

        @Test
        @DisplayName("should delete party successfully")
        void shouldDeletePartySuccessfully() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(partyRepository.findByUser_IdAndId(1L, 1L)).thenReturn(Optional.of(testParty));

            // Act
            partyService.deleteParty(1L, 1L);

            // Assert
            verify(partyRepository).delete(testParty);
        }
    }

    // ==================== Search Parties Tests ====================

    @Nested
    @DisplayName("Search Parties")
    class SearchParties {

        @Test
        @DisplayName("should search parties by normalized prefix")
        void shouldSearchPartiesByNormalizedPrefix() {
            // Arrange: Search for "ama" should find "Amazon"
            Party amazon = new Party();
            amazon.setId(1L);
            amazon.setName("Amazon");
            amazon.setNormalizedName("amazon");

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(partyRepository.searchByNameStartingWith(1L, "ama"))
                    .thenReturn(List.of(amazon));
            when(partyMapper.toResponse(any(Party.class))).thenReturn(testPartyResponse);

            // Act
            List<PartyResponse> results = partyService.searchParties(1L, "AMA");

            // Assert: Search term should be normalized
            verify(partyRepository).searchByNameStartingWith(1L, "ama");
            assertThat(results).hasSize(1);
        }

        @Test
        @DisplayName("should return all parties when search term is null")
        void shouldReturnAllPartiesWhenSearchTermIsNull() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(partyRepository.searchByNameStartingWith(1L, ""))
                    .thenReturn(List.of(testParty));
            when(partyMapper.toResponse(any(Party.class))).thenReturn(testPartyResponse);

            // Act
            List<PartyResponse> results = partyService.searchParties(1L, null);

            // Assert: Empty string search (returns all)
            verify(partyRepository).searchByNameStartingWith(1L, "");
        }
    }

    // ==================== Find Or Create Party Tests ====================

    @Nested
    @DisplayName("Find Or Create Party")
    class FindOrCreateParty {

        @Test
        @DisplayName("should return existing party when found")
        void shouldReturnExistingPartyWhenFound() {
            // Arrange: Party "Amazon" already exists
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(partyRepository.findByUser_IdAndNormalizedName(1L, "amazon"))
                    .thenReturn(Optional.of(testParty));

            // Act
            Party result = partyService.findOrCreateParty(1L, "AMAZON");

            // Assert: Should return existing, not create new
            assertThat(result).isEqualTo(testParty);
            verify(partyRepository, never()).save(any(Party.class));
        }

        @Test
        @DisplayName("should create new party when not found")
        void shouldCreateNewPartyWhenNotFound() {
            // Arrange: Party doesn't exist
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(partyRepository.findByUser_IdAndNormalizedName(1L, "new shop"))
                    .thenReturn(Optional.empty());
            when(partyRepository.save(any(Party.class))).thenAnswer(inv -> {
                Party p = inv.getArgument(0);
                p.setId(5L);
                return p;
            });

            // Act
            Party result = partyService.findOrCreateParty(1L, "New Shop");

            // Assert: New party created
            ArgumentCaptor<Party> partyCaptor = ArgumentCaptor.forClass(Party.class);
            verify(partyRepository).save(partyCaptor.capture());

            Party savedParty = partyCaptor.getValue();
            assertThat(savedParty.getName()).isEqualTo("New Shop");
            assertThat(savedParty.getNormalizedName()).isEqualTo("new shop");
        }

        @Test
        @DisplayName("should return null for null party name")
        void shouldReturnNullForNullPartyName() {
            // Act
            Party result = partyService.findOrCreateParty(1L, null);

            // Assert
            assertThat(result).isNull();
            verify(partyRepository, never()).findByUser_IdAndNormalizedName(anyLong(), anyString());
        }

        @Test
        @DisplayName("should return null for empty party name")
        void shouldReturnNullForEmptyPartyName() {
            // Act
            Party result = partyService.findOrCreateParty(1L, "   ");

            // Assert
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should trim party name when creating")
        void shouldTrimPartyNameWhenCreating() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(partyRepository.findByUser_IdAndNormalizedName(1L, "target"))
                    .thenReturn(Optional.empty());
            when(partyRepository.save(any(Party.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            partyService.findOrCreateParty(1L, "  Target  ");

            // Assert: Name should be trimmed
            ArgumentCaptor<Party> partyCaptor = ArgumentCaptor.forClass(Party.class);
            verify(partyRepository).save(partyCaptor.capture());
            assertThat(partyCaptor.getValue().getName()).isEqualTo("Target");
        }
    }

    // ==================== Get Parties Tests ====================

    @Nested
    @DisplayName("Get Parties")
    class GetParties {

        @Test
        @DisplayName("should return all parties for user")
        void shouldReturnAllPartiesForUser() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(partyRepository.searchByNameStartingWith(1L, ""))
                    .thenReturn(List.of(testParty));
            when(partyMapper.toResponse(testParty)).thenReturn(testPartyResponse);

            // Act
            List<PartyResponse> parties = partyService.getParties(1L);

            // Assert
            assertThat(parties).hasSize(1);
        }

        @Test
        @DisplayName("should get single party by ID")
        void shouldGetSinglePartyById() {
            // Arrange
            when(partyRepository.findByUser_IdAndId(1L, 1L))
                    .thenReturn(Optional.of(testParty));
            when(partyMapper.toResponse(testParty)).thenReturn(testPartyResponse);

            // Act
            PartyResponse party = partyService.getParty(1L, 1L);

            // Assert
            assertThat(party).isNotNull();
            assertThat(party.id()).isEqualTo(1L);
        }

        @Test
        @DisplayName("should throw exception when party not found")
        void shouldThrowExceptionWhenPartyNotFound() {
            // Arrange
            when(partyRepository.findByUser_IdAndId(1L, 999L))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> partyService.getParty(1L, 999L))
                    .isInstanceOf(PartyNotFoundException.class);
        }
    }
}
