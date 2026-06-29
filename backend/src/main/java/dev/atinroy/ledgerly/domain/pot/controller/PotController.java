package dev.atinroy.ledgerly.domain.pot.controller;

import dev.atinroy.ledgerly.domain.pot.dto.PotCreateRequest;
import dev.atinroy.ledgerly.domain.pot.dto.PotUpdateRequest;
import dev.atinroy.ledgerly.domain.pot.dto.PotResponse;
import dev.atinroy.ledgerly.security.AuthenticatedUserId;
import dev.atinroy.ledgerly.domain.pot.service.PotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/pots")
@RequiredArgsConstructor
public class PotController {

    private final PotService potService;

    @PostMapping
    public ResponseEntity<PotResponse> createPot(
            @AuthenticatedUserId Long authenticatedUserId,
            @PathVariable Long userId,
            @Valid @RequestBody PotCreateRequest request) {
        requireOwnership(authenticatedUserId, userId);
        PotResponse response = potService.createPot(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PotResponse>> getPots(
            @AuthenticatedUserId Long authenticatedUserId,
            @PathVariable Long userId) {
        requireOwnership(authenticatedUserId, userId);
        List<PotResponse> responses = potService.getPots(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{potId}")
    public ResponseEntity<PotResponse> getPot(
            @AuthenticatedUserId Long authenticatedUserId,
            @PathVariable Long userId,
            @PathVariable Long potId) {
        requireOwnership(authenticatedUserId, userId);
        PotResponse response = potService.getPot(userId, potId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{potId}")
    public ResponseEntity<PotResponse> updatePot(
            @AuthenticatedUserId Long authenticatedUserId,
            @PathVariable Long userId,
            @PathVariable Long potId,
            @Valid @RequestBody PotUpdateRequest request) {
        requireOwnership(authenticatedUserId, userId);
        PotResponse response = potService.updatePot(userId, potId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{potId}")
    public ResponseEntity<Void> deletePot(
            @AuthenticatedUserId Long authenticatedUserId,
            @PathVariable Long userId,
            @PathVariable Long potId) {
        requireOwnership(authenticatedUserId, userId);
        potService.deletePot(userId, potId);
        return ResponseEntity.noContent().build();
    }

    private void requireOwnership(Long authenticatedUserId, Long userId) {
        if (!authenticatedUserId.equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }
    }
}
