package dev.atinroy.financebackend.service;

import dev.atinroy.financebackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final PotRepository potRepository;
    private final PartyRepository partyRepository;
    private final BudgetRepository budgetRepository;
    private final BillRepository billRepository;

    @Autowired
    public UserService(
        UserRepository userRepository,
        TransactionRepository transactionRepository,
        PotRepository potRepository,
        PartyRepository partyRepository,
        BudgetRepository budgetRepository,
        BillRepository billRepository
        ) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.potRepository = potRepository;
        this.partyRepository = partyRepository;
        this.budgetRepository = budgetRepository;
        this.billRepository = billRepository;
    }

}
