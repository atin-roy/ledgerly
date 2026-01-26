package dev.atinroy.ledgerly.controller;

import dev.atinroy.ledgerly.dto.request.PartyCreateRequest;
import dev.atinroy.ledgerly.dto.request.PartyUpdateRequest;
import dev.atinroy.ledgerly.dto.response.PartyResponse;
import dev.atinroy.ledgerly.service.PartyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/parties")
@RequiredArgsConstructor
public class PartyController {

    private final PartyService partyService;

    @PostMapping
    public ResponseEntity<PartyResponse> createParty(
            @PathVariable Long userId,
            @RequestBody PartyCreateRequest request) {
        PartyResponse response = partyService.createParty(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PartyResponse>> getParties(@PathVariable Long userId) {
        List<PartyResponse> responses = partyService.getParties(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PartyResponse>> searchParties(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "") String q) {
        List<PartyResponse> responses = partyService.searchParties(userId, q);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{partyId}")
    public ResponseEntity<PartyResponse> getParty(
            @PathVariable Long userId,
            @PathVariable Long partyId) {
        PartyResponse response = partyService.getParty(userId, partyId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{partyId}")
    public ResponseEntity<PartyResponse> updateParty(
            @PathVariable Long userId,
            @PathVariable Long partyId,
            @RequestBody PartyUpdateRequest request) {
        PartyResponse response = partyService.updateParty(userId, partyId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{partyId}")
    public ResponseEntity<Void> deleteParty(
            @PathVariable Long userId,
            @PathVariable Long partyId) {
        partyService.deleteParty(userId, partyId);
        return ResponseEntity.noContent().build();
    }
}
