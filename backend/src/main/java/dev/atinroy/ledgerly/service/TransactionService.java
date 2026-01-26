package dev.atinroy.ledgerly.service;

import dev.atinroy.ledgerly.dto.request.transaction.TransactionCreateRequest;
import dev.atinroy.ledgerly.dto.request.transaction.TransactionUpdateRequest;
import dev.atinroy.ledgerly.dto.response.TransactionResponse;
import dev.atinroy.ledgerly.entity.Transaction;
import dev.atinroy.ledgerly.entity.User;
import dev.atinroy.ledgerly.error.*;
import dev.atinroy.ledgerly.mapper.TransactionMapper;
import dev.atinroy.ledgerly.repository.*;
import dev.atinroy.ledgerly.validator.TransactionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final TransactionMapper transactionMapper;
    private final TransactionValidator transactionValidator;
    private final CategoryRepository categoryRepository;

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

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        Transaction transaction = transactionRepository.findByUser_IdAndId(userId, transactionId).orElseThrow(() -> new TransactionNotFoundException(transactionId));

        if (request.date() != null) {
            transaction.setDate(request.date());
        }
        if (request.amount() != null) {
            transaction.setAmount(request.amount());
        }
        if (request.categoryId() != null) {
            transaction.setCategory(categoryRepository.findById(request.categoryId()).orElseThrow(() -> new CategoryNotFoundException(request.categoryId())));
        }

        Transaction saved = transactionRepository.save(transaction);
        return transactionMapper.toResponse(saved);
    }

    @Transactional
    public void deleteTransaction(Long userId, Long id) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        Transaction transaction = transactionRepository.findByUser_IdAndId(userId, id)
                .orElseThrow(() -> new TransactionNotFoundException(id));

        transactionRepository.delete(transaction);
    }

    public TransactionResponse getTransaction(Long userId, Long id) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        Transaction transaction = transactionRepository.findByUser_IdAndId(userId, id)
                .orElseThrow(() -> new TransactionNotFoundException(id));

        return transactionMapper.toResponse(transaction);
    }

    public List<TransactionResponse> getTransactions(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        List<Transaction> transactions = transactionRepository.findByUser_Id(userId);

        return transactions.stream()
                .map(transactionMapper::toResponse)
                .toList();
    }
}
