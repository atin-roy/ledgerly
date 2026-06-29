package dev.atinroy.ledgerly.domain.party.controller;

import dev.atinroy.ledgerly.domain.party.dto.PartyCreateRequest;
import dev.atinroy.ledgerly.domain.party.dto.PartyUpdateRequest;
import dev.atinroy.ledgerly.domain.party.dto.PartyResponse;
import dev.atinroy.ledgerly.security.AuthenticatedUserId;
import dev.atinroy.ledgerly.domain.party.service.PartyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/parties")
@RequiredArgsConstructor
public class PartyController {

    private final PartyService partyService;

    @PostMapping
    public ResponseEntity<PartyResponse> createParty(
            @AuthenticatedUserId Long authenticatedUserId,
            @PathVariable Long userId,
            @Valid @RequestBody PartyCreateRequest request) {
        requireOwnership(authenticatedUserId, userId);
        PartyResponse response = partyService.createParty(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PartyResponse>> getParties(
            @AuthenticatedUserId Long authenticatedUserId,
            @PathVariable Long userId) {
        requireOwnership(authenticatedUserId, userId);
        List<PartyResponse> responses = partyService.getParties(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PartyResponse>> searchParties(
            @AuthenticatedUserId Long authenticatedUserId,
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "") String q) {
        requireOwnership(authenticatedUserId, userId);
        List<PartyResponse> responses = partyService.searchParties(userId, q);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{partyId}")
    public ResponseEntity<PartyResponse> getParty(
            @AuthenticatedUserId Long authenticatedUserId,
            @PathVariable Long userId,
            @PathVariable Long partyId) {
        requireOwnership(authenticatedUserId, userId);
        PartyResponse response = partyService.getParty(userId, partyId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{partyId}")
    public ResponseEntity<PartyResponse> updateParty(
            @AuthenticatedUserId Long authenticatedUserId,
            @PathVariable Long userId,
            @PathVariable Long partyId,
            @Valid @RequestBody PartyUpdateRequest request) {
        requireOwnership(authenticatedUserId, userId);
        PartyResponse response = partyService.updateParty(userId, partyId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{partyId}")
    public ResponseEntity<Void> deleteParty(
            @AuthenticatedUserId Long authenticatedUserId,
            @PathVariable Long userId,
            @PathVariable Long partyId) {
        requireOwnership(authenticatedUserId, userId);
        partyService.deleteParty(userId, partyId);
        return ResponseEntity.noContent().build();
    }

    private void requireOwnership(Long authenticatedUserId, Long userId) {
        if (!authenticatedUserId.equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }
    }
}
