package dev.atinroy.ledgerly.mapper;

import dev.atinroy.ledgerly.dto.request.transaction.TransactionCreateRequest;
import dev.atinroy.ledgerly.dto.response.TransactionResponse;
import dev.atinroy.ledgerly.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Transaction toEntity(TransactionCreateRequest transactionCreateRequest);

    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "party.name", target = "partyName")
    TransactionResponse toResponse(Transaction transaction);
}
