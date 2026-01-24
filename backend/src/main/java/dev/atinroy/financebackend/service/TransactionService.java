package dev.atinroy.financebackend.service;

import dev.atinroy.financebackend.dto.request.TransactionCreateRequest;
import dev.atinroy.financebackend.entity.*;
import dev.atinroy.financebackend.exception.BudgetNotFoundException;
import dev.atinroy.financebackend.exception.TransactionTypeNotFound;
import dev.atinroy.financebackend.exception.UserNotFoundException;
import dev.atinroy.financebackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final PartyRepository partyRepository;
    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;
    private final SystemTransactionTypeRepository systemTransactionTypeRepository;
    private final UserTransactionTypeRepository userTransactionTypeRepository;

    @Transactional
    public Transaction createTransaction(TransactionCreateRequest request, Long userId, Long budgetId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Party party = null;
        if (request.getPartyName() != null && !request.getPartyName().isBlank()) {
            String partyName = request.getPartyName().trim().toLowerCase(Locale.ROOT);

            party = partyRepository
                    .findByUser_UserIdAndPartyNameIgnoreCase(userId, partyName)
                    .orElseGet(() -> {
                        Party newParty = new Party();
                        newParty.setPartyName(partyName);
                        newParty.setUser(user);
                        return partyRepository.save(newParty);
                    });
        }

        Budget budget = budgetRepository.findByUser_UserIdAndBudgetId(userId, budgetId).orElseThrow(() -> new BudgetNotFoundException(budgetId));

        Transaction transaction = new Transaction();
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setTransactionAmount(request.getTransactionAmount());
        if (request.getDescription() != null && !request.getDescription().trim().isEmpty()) {
            transaction.setDescription(request.getDescription().trim());
        }
        transaction.setBudget(budget);
        transaction.setParty(party);
        transaction.setUser(user);


        // Set the appropriate transaction type
        if (request.getTransactionTypeId() == null) {
            throw new IllegalArgumentException("Transaction type is required");
        }
        if (transaction.getSystemTransactionType() != null &&
                transaction.getUserTransactionType() != null) {
            throw new IllegalStateException(
                    "Transaction cannot have both system and user transaction types"
            );
        }

        if (Boolean.TRUE.equals(request.getIsSystemType())) {
            SystemTransactionType systemType = systemTransactionTypeRepository.findById(request.getTransactionTypeId())
                    .orElseThrow(() -> new TransactionTypeNotFound("System transaction type not found with id: " + request.getTransactionTypeId()));
            transaction.setSystemTransactionType(systemType);
        } else {
            UserTransactionType userType = userTransactionTypeRepository.findByUser_UserIdAndTransactionTypeId(userId, request.getTransactionTypeId())
                    .orElseThrow(() -> new TransactionTypeNotFound("User transaction type not found with id: " + request.getTransactionTypeId()));
            transaction.setUserTransactionType(userType);
        }

        return transactionRepository.save(transaction);
    }
}
