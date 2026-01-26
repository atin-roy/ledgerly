package dev.atinroy.ledgerly.mapper;

import dev.atinroy.ledgerly.dto.request.CategoryCreateRequest;
import dev.atinroy.ledgerly.dto.response.CategoryResponse;
import dev.atinroy.ledgerly.entity.Category;
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

    @Mapping(source = "id", target = "categoryId")
    CategoryResponse toResponse(Category category);
}
