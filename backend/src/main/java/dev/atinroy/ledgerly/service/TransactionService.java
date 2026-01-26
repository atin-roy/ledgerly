package dev.atinroy.ledgerly.service;

import dev.atinroy.ledgerly.dto.request.transaction.TransactionCreateRequest;
import dev.atinroy.ledgerly.dto.response.TransactionResponse;
import dev.atinroy.ledgerly.entity.Transaction;
import dev.atinroy.ledgerly.error.ErrorCode;
import dev.atinroy.ledgerly.error.ValidationException;
import dev.atinroy.ledgerly.error.ValidationResult;
import dev.atinroy.ledgerly.mapper.TransactionMapper;
import dev.atinroy.ledgerly.repository.*;
import dev.atinroy.ledgerly.validator.TransactionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final TransactionMapper transactionMapper;
    private final TransactionValidator transactionValidator;

    public TransactionResponse createTransaction(TransactionCreateRequest request) {
        if (request == null) {
            ValidationResult result = ValidationResult.withErrors();
            result.addGeneralError(ErrorCode.REQUIRED, "Request is required");
            throw new ValidationException(result);
        }

        ValidationResult result = ValidationResult.withErrors();
        result.merge(transactionValidator.validate(request));

        if (result.hasErrors()) {
            throw new ValidationException(result);
        }

        Transaction newTransaction = transactionRepository.save(transactionMapper.toEntity(request));
        return transactionMapper.toResponse(newTransaction);
    }
}
