package dev.atinroy.financebackend.service;

import dev.atinroy.financebackend.repository.BudgetRepository;
import dev.atinroy.financebackend.repository.PartyRepository;
import dev.atinroy.financebackend.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final PartyRepository partyRepository;
    private final BudgetRepository budgetRepository;
}
