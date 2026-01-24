package dev.atinroy.financebackend.mapper;

import dev.atinroy.financebackend.dto.request.TransactionCreateRequest;
import dev.atinroy.financebackend.dto.response.TransactionCreatedResponse;
import dev.atinroy.financebackend.entity.Transaction;
import org.mapstruct.Mapping;

public interface TransactionMapper {

    @Mapping(target = "transactionId", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "budget", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Transaction toEntity(TransactionCreateRequest transactionCreateRequest);

    @Mapping(source = "budget.budgetId", target = "budgetId")
    TransactionCreatedResponse toResponse(Transaction transaction);
}
