package dev.atinroy.ledgerly.controller;

import dev.atinroy.ledgerly.dto.request.BillCreateRequest;
import dev.atinroy.ledgerly.dto.request.BillUpdateRequest;
import dev.atinroy.ledgerly.dto.response.BillResponse;
import dev.atinroy.ledgerly.entity.enums.BillStatus;
import dev.atinroy.ledgerly.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            @PathVariable Long userId,
            @RequestBody BillCreateRequest request) {
        BillResponse response = billService.createBill(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<BillResponse>> getBills(@PathVariable Long userId) {
        List<BillResponse> responses = billService.getBills(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{billId}")
    public ResponseEntity<BillResponse> getBill(
            @PathVariable Long userId,
            @PathVariable Long billId) {
        BillResponse response = billService.getBill(userId, billId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{billId}")
    public ResponseEntity<BillResponse> updateBill(
            @PathVariable Long userId,
            @PathVariable Long billId,
            @RequestBody BillUpdateRequest request) {
        BillResponse response = billService.updateBill(userId, billId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{billId}")
    public ResponseEntity<Void> deleteBill(
            @PathVariable Long userId,
            @PathVariable Long billId) {
        billService.deleteBill(userId, billId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countBillsByStatus(
            @PathVariable Long userId,
            @RequestParam BillStatus status) {
        long count = billService.countBillsByStatus(userId, status);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/sum")
    public ResponseEntity<BigDecimal> sumBillsByStatus(
            @PathVariable Long userId,
            @RequestParam BillStatus status) {
        BigDecimal sum = billService.sumBillsByStatus(userId, status);
        return ResponseEntity.ok(sum);
    }
}
