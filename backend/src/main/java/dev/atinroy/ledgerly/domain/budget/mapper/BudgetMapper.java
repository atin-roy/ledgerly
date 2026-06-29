package dev.atinroy.ledgerly.domain.budget.mapper;

import dev.atinroy.ledgerly.domain.budget.dto.BudgetCreateRequest;
import dev.atinroy.ledgerly.domain.budget.entity.Budget;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BudgetMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Budget toEntity(BudgetCreateRequest budgetCreateRequest);
}
