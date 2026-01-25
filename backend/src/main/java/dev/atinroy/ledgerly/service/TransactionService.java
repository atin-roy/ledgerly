package dev.atinroy.ledgerly.service;

import dev.atinroy.ledgerly.dto.request.transaction.TransactionCreateRequest;
import dev.atinroy.ledgerly.dto.request.transaction.TransactionPatchRequest;
import dev.atinroy.ledgerly.entity.*;
import dev.atinroy.ledgerly.exception.BudgetNotFoundException;
import dev.atinroy.ledgerly.exception.TransactionNotFoundException;
import dev.atinroy.ledgerly.exception.TransactionTypeNotFoundException;
import dev.atinroy.ledgerly.exception.UserNotFoundException;
import dev.atinroy.ledgerly.mapper.TransactionMapper;
import dev.atinroy.ledgerly.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final PartyRepository partyRepository;
    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;
    private final TransactionTypeRepository transactionTypeRepository;
    private final TransactionMapper transactionMapper;

    @Transactional
    public Transaction createTransaction(TransactionCreateRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Party party = null;
        if (request.getPartyName() != null && !request.getPartyName().isBlank()) {
            String partyName = request.getPartyName().trim().toLowerCase(Locale.ROOT);

            party = partyRepository
                    .findByUser_UserIdAndNameIgnoreCase(userId, partyName)
                    .orElseGet(() -> {
                        Party newParty = new Party();
                        newParty.setName(partyName);
                        newParty.setUser(user);
                        return partyRepository.save(newParty);
                    });
        }

        Budget budget = budgetRepository.findByUser_UserIdAndBudgetId(userId, request.getBudgetId()).orElseThrow(() -> new BudgetNotFoundException(request.getBudgetId()));

        TransactionType transactionType = transactionTypeRepository.findByUser_UserIdAndTransactionTypeId(userId, request.getTypeId())
                .orElseThrow(() -> new TransactionTypeNotFoundException("Transaction type not found with id: " + request.getTypeId()));

        Transaction transaction = transactionMapper.toEntity(request);
        transaction.setUser(user);
        transaction.setParty(party);
        transaction.setBudget(budget);
        transaction.setType(transactionType);

        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction patchTransaction(TransactionPatchRequest request, Long userId, Long transactionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Transaction transaction =
                transactionRepository
                        .findByTransactionIdAndUser_UserId(userId, transactionId)
                        .orElseThrow(TransactionNotFoundException::new);

        if (request.getAmount() != null) {
            transaction.setAmount(request.getAmount());
        }
        if (request.getDate() != null) {
            transaction.setDate(request.getDate());
        }
        if (request.getTypeId() != null) {
            TransactionType transactionType = transactionTypeRepository.findByUser_UserIdAndTransactionTypeId(userId, request.getTypeId()).orElseThrow(() -> new TransactionTypeNotFoundException("Transaction type not found with id: " + request.getTypeId()));
            transaction.setType(transactionType);
        }
        if (request.getDescription() != null) {
            transaction.setDescription(request.getDescription());
        }
        if (request.getPartyName() != null && !request.getPartyName().isBlank()) {
            String partyName = request.getPartyName().trim().toLowerCase(Locale.ROOT);
            Party party = partyRepository.findByUser_UserIdAndNameIgnoreCase(userId, partyName).orElse(null);
            if (party != null) {
                transaction.setParty(party);
            } else {
                Party newParty = new Party();
                newParty.setName(partyName);
                newParty.setUser(user);
            }
        }

        return transactionRepository.save(transaction);
    }

    @Transactional(readOnly = true)
    public Page<Transaction> getTransactionsForUser(Long userId, Pageable pageable) {
        return transactionRepository.findByUser_UserId(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Transaction> getTransactionsForUserForBudget(Long userId, Long budgetId, Pageable pageable) {
        return transactionRepository.findByUser_UserIdAndBudget_BudgetId(userId, budgetId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Transaction> getTransactionsForUserForTransactionType(Long userId, Long transactionTypeId, Pageable pageable) {
        return transactionRepository.findByUser_UserIdAndType_TransactionTypeId(userId, transactionTypeId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Transaction> getTransactionsBetweenDates(Long userId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return transactionRepository.findByUser_UserIdAndDateBetween(userId, startDate, endDate, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Transaction> getTransactionsBetweenAmounts(Long userId, BigDecimal from, BigDecimal to, Pageable pageable) {
        return transactionRepository.findByUser_UserIdAndAmountBetween(userId, from, to, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Transaction> getTransactionsForUserForParty(Long userId, Long partyId, Pageable pageable) {
        return  transactionRepository.findByUser_UserIdAndParty_PartyId(userId, partyId, pageable);
    }
}
