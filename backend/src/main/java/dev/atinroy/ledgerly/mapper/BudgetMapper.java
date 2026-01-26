package dev.atinroy.ledgerly.mapper;

import dev.atinroy.ledgerly.dto.request.BudgetCreateRequest;
import dev.atinroy.ledgerly.dto.response.BudgetResponse;
import dev.atinroy.ledgerly.entity.Budget;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BudgetMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Budget toEntity(BudgetCreateRequest budgetCreateRequest);

    BudgetResponse toResponse(Budget budget);
}
