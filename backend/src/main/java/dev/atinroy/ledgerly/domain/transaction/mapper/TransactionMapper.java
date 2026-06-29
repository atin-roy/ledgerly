package dev.atinroy.ledgerly.domain.transaction.mapper;

import dev.atinroy.ledgerly.domain.transaction.dto.TransactionCreateRequest;
import dev.atinroy.ledgerly.domain.transaction.dto.TransactionResponse;
import dev.atinroy.ledgerly.domain.transaction.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Transaction toEntity(TransactionCreateRequest transactionCreateRequest);

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "party.name", target = "partyName")
    TransactionResponse toResponse(Transaction transaction);
}
