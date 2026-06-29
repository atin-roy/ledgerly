package dev.atinroy.ledgerly.domain.category.mapper;

import dev.atinroy.ledgerly.domain.category.dto.CategoryCreateRequest;
import dev.atinroy.ledgerly.domain.category.dto.CategoryResponse;
import dev.atinroy.ledgerly.domain.category.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "budget", ignore = true)
    Category toEntity(CategoryCreateRequest categoryCreateRequest);

    CategoryResponse toResponse(Category category);
}
