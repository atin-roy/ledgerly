package dev.atinroy.ledgerly.controller;

import dev.atinroy.ledgerly.dto.request.transaction.TransactionCreateRequest;
import dev.atinroy.ledgerly.dto.request.transaction.TransactionUpdateRequest;
import dev.atinroy.ledgerly.dto.response.TransactionResponse;
import dev.atinroy.ledgerly.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/{userId}/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @PathVariable Long userId,
            @RequestBody TransactionCreateRequest request) {
        TransactionResponse response = transactionService.createTransaction(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> getTransactions(
            @PathVariable Long userId,
            Pageable pageable) {
        Page<TransactionResponse> responses = transactionService.getTransactions(userId, pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransaction(
            @PathVariable Long userId,
            @PathVariable Long transactionId) {
        TransactionResponse response = transactionService.getTransaction(userId, transactionId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @PathVariable Long userId,
            @PathVariable Long transactionId,
            @RequestBody TransactionUpdateRequest request) {
        TransactionResponse response = transactionService.updateTransaction(userId, transactionId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable Long userId,
            @PathVariable Long transactionId) {
        transactionService.deleteTransaction(userId, transactionId);
        return ResponseEntity.noContent().build();
    }
}
