package dev.atinroy.financebackend.mapper;

import dev.atinroy.financebackend.dto.request.transaction.TransactionCreateRequest;
import dev.atinroy.financebackend.dto.response.TransactionCreatedResponse;
import dev.atinroy.financebackend.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "transactionId", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "transactionType", ignore = true)
    Transaction toEntity(TransactionCreateRequest transactionCreateRequest);

    TransactionCreatedResponse toResponse(Transaction transaction);
}
