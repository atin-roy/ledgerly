package dev.atinroy.financebackend.mapper;

import dev.atinroy.financebackend.dto.request.UserCreateRequest;
import dev.atinroy.financebackend.dto.response.UserCreateResponse;
import dev.atinroy.financebackend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    // Request -> Entity
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(UserCreateRequest request);

    UserCreateResponse toDto(User user);
}
