package dev.atinroy.ledgerly.controller;

import dev.atinroy.ledgerly.dto.request.PotCreateRequest;
import dev.atinroy.ledgerly.dto.request.PotUpdateRequest;
import dev.atinroy.ledgerly.dto.response.PotResponse;
import dev.atinroy.ledgerly.service.PotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/pots")
@RequiredArgsConstructor
public class PotController {

    private final PotService potService;

    @PostMapping
    public ResponseEntity<PotResponse> createPot(
            @PathVariable Long userId,
            @RequestBody PotCreateRequest request) {
        PotResponse response = potService.createPot(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PotResponse>> getPots(@PathVariable Long userId) {
        List<PotResponse> responses = potService.getPots(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{potId}")
    public ResponseEntity<PotResponse> getPot(
            @PathVariable Long userId,
            @PathVariable Long potId) {
        PotResponse response = potService.getPot(userId, potId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{potId}")
    public ResponseEntity<PotResponse> updatePot(
            @PathVariable Long userId,
            @PathVariable Long potId,
            @RequestBody PotUpdateRequest request) {
        PotResponse response = potService.updatePot(userId, potId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{potId}")
    public ResponseEntity<Void> deletePot(
            @PathVariable Long userId,
            @PathVariable Long potId) {
        potService.deletePot(userId, potId);
        return ResponseEntity.noContent().build();
    }
}
