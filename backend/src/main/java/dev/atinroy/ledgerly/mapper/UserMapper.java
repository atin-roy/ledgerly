package dev.atinroy.ledgerly.mapper;

import dev.atinroy.ledgerly.dto.request.UserCreateRequest;
import dev.atinroy.ledgerly.dto.response.UserResponse;
import dev.atinroy.ledgerly.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(UserCreateRequest request);

    UserResponse toResponse(User user);
}
