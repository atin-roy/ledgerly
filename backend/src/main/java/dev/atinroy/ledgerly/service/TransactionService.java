package dev.atinroy.ledgerly.service;

import dev.atinroy.ledgerly.dto.request.transaction.TransactionCreateRequest;
import dev.atinroy.ledgerly.dto.request.transaction.TransactionUpdateRequest;
import dev.atinroy.ledgerly.dto.response.TransactionResponse;
import dev.atinroy.ledgerly.entity.Category;
import dev.atinroy.ledgerly.entity.Transaction;
import dev.atinroy.ledgerly.entity.User;
import dev.atinroy.ledgerly.error.ErrorCode;
import dev.atinroy.ledgerly.error.TransactionNotFoundException;
import dev.atinroy.ledgerly.error.UserNotFoundException;
import dev.atinroy.ledgerly.error.ValidationException;
import dev.atinroy.ledgerly.error.ValidationResult;
import dev.atinroy.ledgerly.mapper.TransactionMapper;
import dev.atinroy.ledgerly.repository.*;
import dev.atinroy.ledgerly.validator.TransactionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryService categoryService;
    private final TransactionMapper transactionMapper;
    private final TransactionValidator transactionValidator;

    @Transactional
    public TransactionResponse createTransaction(Long userId, TransactionCreateRequest request) {
        if (request == null) {
            ValidationResult result = ValidationResult.withErrors();
            result.addGeneralError(ErrorCode.REQUIRED, "Request is required");
            throw new ValidationException(result);
        }

        ValidationResult result = transactionValidator.validate(request);

        if (result.hasErrors()) {
            throw new ValidationException(result);
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        Transaction transaction = transactionMapper.toEntity(request);
        transaction.setUser(user);

        // Resolve category ID (0 maps to General category)
        Category category = categoryService.findOrResolveGeneralCategory(userId, request.categoryId());
        transaction.setCategory(category);

        Transaction saved = transactionRepository.save(transaction);
        return transactionMapper.toResponse(saved);
    }

    @Transactional
    public TransactionResponse updateTransaction(Long userId, Long transactionId, TransactionUpdateRequest request) {
        if (request == null) {
            ValidationResult result = ValidationResult.withErrors();
            result.addGeneralError(ErrorCode.REQUIRED, "Request is required");
            throw new ValidationException(result);
        }
        ValidationResult result = transactionValidator.validate(request);

        if (result.hasErrors()) {
            throw new ValidationException(result);
        }

        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        Transaction transaction = transactionRepository.findByUser_IdAndId(userId, transactionId)
                .orElseThrow(() -> new TransactionNotFoundException(transactionId));

        if (request.date() != null) {
            transaction.setDate(request.date());
        }
        if (request.amount() != null) {
            transaction.setAmount(request.amount());
        }
        if (request.categoryId() != null) {
            Category category = categoryService.findOrResolveGeneralCategory(userId, request.categoryId());
            transaction.setCategory(category);
        }

        Transaction saved = transactionRepository.save(transaction);
        return transactionMapper.toResponse(saved);
    }

    @Transactional
    public void deleteTransaction(Long userId, Long id) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        Transaction transaction = transactionRepository.findByUser_IdAndId(userId, id)
                .orElseThrow(() -> new TransactionNotFoundException(id));

        transactionRepository.delete(transaction);
    }

    public TransactionResponse getTransaction(Long userId, Long id) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        Transaction transaction = transactionRepository.findByUser_IdAndId(userId, id)
                .orElseThrow(() -> new TransactionNotFoundException(id));

        return transactionMapper.toResponse(transaction);
    }

    public Page<TransactionResponse> getTransactions(Long userId, Pageable pageable) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        return transactionRepository.findByUser_Id(userId, pageable)
                .map(transactionMapper::toResponse);
    }
}
