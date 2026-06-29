package dev.atinroy.ledgerly.domain.transaction.service;

import dev.atinroy.ledgerly.domain.transaction.dto.TransactionCreateRequest;
import dev.atinroy.ledgerly.domain.transaction.dto.TransactionUpdateRequest;
import dev.atinroy.ledgerly.domain.transaction.dto.TransactionResponse;
import dev.atinroy.ledgerly.domain.category.entity.Category;
import dev.atinroy.ledgerly.domain.category.service.CategoryService;
import dev.atinroy.ledgerly.domain.party.service.PartyService;
import dev.atinroy.ledgerly.domain.transaction.entity.Transaction;
import dev.atinroy.ledgerly.domain.user.entity.User;
import dev.atinroy.ledgerly.shared.error.ErrorCode;
import dev.atinroy.ledgerly.domain.transaction.exception.TransactionNotFoundException;
import dev.atinroy.ledgerly.domain.user.exception.UserNotFoundException;
import dev.atinroy.ledgerly.shared.error.ValidationException;
import dev.atinroy.ledgerly.shared.error.ValidationResult;
import dev.atinroy.ledgerly.domain.transaction.mapper.TransactionMapper;
import dev.atinroy.ledgerly.domain.category.repository.CategoryRepository;
import dev.atinroy.ledgerly.domain.transaction.repository.TransactionRepository;
import dev.atinroy.ledgerly.domain.user.repository.UserRepository;
import dev.atinroy.ledgerly.domain.transaction.validator.TransactionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryService categoryService;
    private final PartyService partyService;
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

        // Handle party name - find existing or create new party
        if (request.partyName() != null && !request.partyName().trim().isEmpty()) {
            var party = partyService.findOrCreateParty(userId, request.partyName());
            transaction.setParty(party);
        }

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
        if (request.partyName() != null) {
            if (request.partyName().trim().isEmpty()) {
                // Empty string means remove the party
                transaction.setParty(null);
            } else {
                // Find or create party
                var party = partyService.findOrCreateParty(userId, request.partyName());
                transaction.setParty(party);
            }
        }
        if (request.description() != null) {
            transaction.setDescription(request.description());
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

    @Transactional(readOnly = true)
    public TransactionResponse getTransaction(Long userId, Long id) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        Transaction transaction = transactionRepository.findByUser_IdAndId(userId, id)
                .orElseThrow(() -> new TransactionNotFoundException(id));

        return transactionMapper.toResponse(transaction);
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> getTransactions(Long userId, Pageable pageable) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        return transactionRepository.findByUser_Id(userId, pageable)
                .map(transactionMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsList(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        return transactionRepository.findByUser_Id(userId)
                .stream()
                .map(transactionMapper::toResponse)
                .toList();
    }
}
