package dev.atinroy.ledgerly.mapper;

import dev.atinroy.ledgerly.dto.request.BudgetCreateRequest;
import dev.atinroy.ledgerly.dto.response.BudgetCreateResponse;
import dev.atinroy.ledgerly.entity.Budget;
import org.mapstruct.Mapping;

public interface BudgetMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Budget toEntity(BudgetCreateRequest budgetCreateRequest);

    BudgetCreateResponse toResponse(Budget budget);
}
