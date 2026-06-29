package dev.atinroy.ledgerly.domain.bill.controller;

import dev.atinroy.ledgerly.domain.bill.dto.BillCreateRequest;
import dev.atinroy.ledgerly.domain.bill.dto.BillUpdateRequest;
import dev.atinroy.ledgerly.domain.bill.dto.BillResponse;
import dev.atinroy.ledgerly.domain.bill.entity.BillStatus;
import dev.atinroy.ledgerly.security.AuthenticatedUserId;
import dev.atinroy.ledgerly.domain.bill.service.BillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;

    @PostMapping
    public ResponseEntity<BillResponse> createBill(
            @AuthenticatedUserId Long authenticatedUserId,
            @PathVariable Long userId,
            @Valid @RequestBody BillCreateRequest request) {
        requireOwnership(authenticatedUserId, userId);
        BillResponse response = billService.createBill(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<BillResponse>> getBills(
            @AuthenticatedUserId Long authenticatedUserId,
            @PathVariable Long userId) {
        requireOwnership(authenticatedUserId, userId);
        List<BillResponse> responses = billService.getBills(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{billId}")
    public ResponseEntity<BillResponse> getBill(
            @AuthenticatedUserId Long authenticatedUserId,
            @PathVariable Long userId,
            @PathVariable Long billId) {
        requireOwnership(authenticatedUserId, userId);
        BillResponse response = billService.getBill(userId, billId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{billId}")
    public ResponseEntity<BillResponse> updateBill(
            @AuthenticatedUserId Long authenticatedUserId,
            @PathVariable Long userId,
            @PathVariable Long billId,
            @Valid @RequestBody BillUpdateRequest request) {
        requireOwnership(authenticatedUserId, userId);
        BillResponse response = billService.updateBill(userId, billId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{billId}")
    public ResponseEntity<Void> deleteBill(
            @AuthenticatedUserId Long authenticatedUserId,
            @PathVariable Long userId,
            @PathVariable Long billId) {
        requireOwnership(authenticatedUserId, userId);
        billService.deleteBill(userId, billId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countBillsByStatus(
            @AuthenticatedUserId Long authenticatedUserId,
            @PathVariable Long userId,
            @RequestParam BillStatus status) {
        requireOwnership(authenticatedUserId, userId);
        long count = billService.countBillsByStatus(userId, status);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/sum")
    public ResponseEntity<BigDecimal> sumBillsByStatus(
            @AuthenticatedUserId Long authenticatedUserId,
            @PathVariable Long userId,
            @RequestParam BillStatus status) {
        requireOwnership(authenticatedUserId, userId);
        BigDecimal sum = billService.sumBillsByStatus(userId, status);
        return ResponseEntity.ok(sum);
    }

    private void requireOwnership(Long authenticatedUserId, Long userId) {
        if (!authenticatedUserId.equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }
    }
}
