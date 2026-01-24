package dev.atinroy.financebackend.mapper;

import dev.atinroy.financebackend.dto.request.BudgetCreateRequest;
import dev.atinroy.financebackend.dto.response.BudgetCreateResponse;
import dev.atinroy.financebackend.entity.Budget;
import org.mapstruct.Mapping;

public interface BudgetMapper {

    @Mapping(target = "budgetId", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Budget toEntity(BudgetCreateRequest budgetCreateRequest);

    BudgetCreateResponse toResponse(Budget budget);
}
