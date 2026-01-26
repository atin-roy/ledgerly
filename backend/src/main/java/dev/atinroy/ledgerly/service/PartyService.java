package dev.atinroy.ledgerly.service;

import dev.atinroy.ledgerly.dto.request.PartyCreateRequest;
import dev.atinroy.ledgerly.dto.request.PartyUpdateRequest;
import dev.atinroy.ledgerly.dto.response.PartyResponse;
import dev.atinroy.ledgerly.entity.Party;
import dev.atinroy.ledgerly.entity.User;
import dev.atinroy.ledgerly.error.*;
import dev.atinroy.ledgerly.mapper.PartyMapper;
import dev.atinroy.ledgerly.repository.PartyRepository;
import dev.atinroy.ledgerly.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartyService {
    private final PartyRepository partyRepository;
    private final UserRepository userRepository;
    private final PartyMapper partyMapper;

    @Transactional
    public PartyResponse createParty(Long userId, PartyCreateRequest request) {
        if (request == null) {
            ValidationResult result = ValidationResult.withErrors();
            result.addGeneralError(ErrorCode.REQUIRED, "Request is required");
            throw new ValidationException(result);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        String normalizedName = Party.normalize(request.name());

        // Check for duplicate party name (case-insensitive)
        if (partyRepository.findByUser_IdAndNormalizedName(userId, normalizedName).isPresent()) {
            ValidationResult result = ValidationResult.withErrors();
            result.addFieldError("name", ErrorCode.ALREADY_EXISTS, "A party with this name already exists");
            throw new ValidationException(result);
        }

        Party party = partyMapper.toEntity(request);
        party.setUser(user);
        party.setNormalizedName(normalizedName);

        Party saved = partyRepository.save(party);
        return partyMapper.toResponse(saved);
    }

    @Transactional
    public PartyResponse updateParty(Long userId, Long partyId, PartyUpdateRequest request) {
        if (request == null) {
            ValidationResult result = ValidationResult.withErrors();
            result.addGeneralError(ErrorCode.REQUIRED, "Request is required");
            throw new ValidationException(result);
        }

        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Party party = partyRepository.findByUser_IdAndId(userId, partyId)
                .orElseThrow(() -> new PartyNotFoundException(partyId));

        if (request.name() != null) {
            String newNormalizedName = Party.normalize(request.name());

            // Check for duplicate only if the normalized name is changing
            if (!newNormalizedName.equals(party.getNormalizedName())) {
                if (partyRepository.findByUser_IdAndNormalizedName(userId, newNormalizedName).isPresent()) {
                    ValidationResult result = ValidationResult.withErrors();
                    result.addFieldError("name", ErrorCode.ALREADY_EXISTS, "A party with this name already exists");
                    throw new ValidationException(result);
                }
                party.setNormalizedName(newNormalizedName);
            }
            party.setName(request.name().trim());
        }

        Party saved = partyRepository.save(party);
        return partyMapper.toResponse(saved);
    }

    @Transactional
    public void deleteParty(Long userId, Long partyId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Party party = partyRepository.findByUser_IdAndId(userId, partyId)
                .orElseThrow(() -> new PartyNotFoundException(partyId));

        partyRepository.delete(party);
    }

    public PartyResponse getParty(Long userId, Long partyId) {
        Party party = partyRepository.findByUser_IdAndId(userId, partyId)
                .orElseThrow(() -> new PartyNotFoundException(partyId));
        return partyMapper.toResponse(party);
    }

    public List<PartyResponse> getParties(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<Party> parties = partyRepository.searchByNameStartingWith(userId, "");
        return parties.stream()
                .map(partyMapper::toResponse)
                .toList();
    }

    public List<PartyResponse> searchParties(Long userId, String searchTerm) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        String normalizedSearchTerm = searchTerm != null ? Party.normalize(searchTerm) : "";
        List<Party> parties = partyRepository.searchByNameStartingWith(userId, normalizedSearchTerm);
        return parties.stream()
                .map(partyMapper::toResponse)
                .toList();
    }

    /**
     * Finds an existing party by name or creates a new one if it doesn't exist.
     * Used when creating/updating transactions with a party name.
     *
     * @param userId the user ID
     * @param partyName the party name (will be normalized for lookup)
     * @return the found or newly created party entity
     */
    @Transactional
    public Party findOrCreateParty(Long userId, String partyName) {
        if (partyName == null || partyName.trim().isEmpty()) {
            return null;
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        String normalizedName = Party.normalize(partyName);

        // Try to find existing party
        return partyRepository.findByUser_IdAndNormalizedName(userId, normalizedName)
                .orElseGet(() -> {
                    // Create new party if it doesn't exist
                    Party newParty = new Party();
                    newParty.setName(partyName.trim());
                    newParty.setNormalizedName(normalizedName);
                    newParty.setUser(user);
                    return partyRepository.save(newParty);
                });
    }
}
