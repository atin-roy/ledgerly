package dev.atinroy.ledgerly.domain.transaction.controller;

import dev.atinroy.ledgerly.domain.transaction.dto.TransactionCreateRequest;
import dev.atinroy.ledgerly.domain.transaction.dto.TransactionUpdateRequest;
import dev.atinroy.ledgerly.domain.transaction.dto.TransactionResponse;
import dev.atinroy.ledgerly.security.AuthenticatedUserId;
import dev.atinroy.ledgerly.domain.transaction.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Transaction management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    @Operation(summary = "Create a new transaction", description = "Creates a new transaction for the specified user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transaction created successfully",
                    content = @Content(schema = @Schema(implementation = TransactionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid transaction data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<TransactionResponse> createTransaction(
            @AuthenticatedUserId Long authenticatedUserId,
            @PathVariable Long userId,
            @Valid @RequestBody TransactionCreateRequest request) {
        requireOwnership(authenticatedUserId, userId);
        TransactionResponse response = transactionService.createTransaction(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all transactions", description = "Retrieves all transactions for the specified user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<List<TransactionResponse>> getTransactions(
            @AuthenticatedUserId Long authenticatedUserId,
            @PathVariable Long userId) {
        requireOwnership(authenticatedUserId, userId);
        List<TransactionResponse> responses = transactionService.getTransactionsList(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransaction(
            @AuthenticatedUserId Long authenticatedUserId,
            @PathVariable Long userId,
            @PathVariable Long transactionId) {
        requireOwnership(authenticatedUserId, userId);
        TransactionResponse response = transactionService.getTransaction(userId, transactionId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @AuthenticatedUserId Long authenticatedUserId,
            @PathVariable Long userId,
            @PathVariable Long transactionId,
            @Valid @RequestBody TransactionUpdateRequest request) {
        requireOwnership(authenticatedUserId, userId);
        TransactionResponse response = transactionService.updateTransaction(userId, transactionId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(
            @AuthenticatedUserId Long authenticatedUserId,
            @PathVariable Long userId,
            @PathVariable Long transactionId) {
        requireOwnership(authenticatedUserId, userId);
        transactionService.deleteTransaction(userId, transactionId);
        return ResponseEntity.noContent().build();
    }

    private void requireOwnership(Long authenticatedUserId, Long userId) {
        if (!authenticatedUserId.equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }
    }
}
