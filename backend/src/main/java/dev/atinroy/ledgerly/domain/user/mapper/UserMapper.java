package dev.atinroy.ledgerly.domain.user.mapper;

import dev.atinroy.ledgerly.domain.user.dto.UserCreateRequest;
import dev.atinroy.ledgerly.domain.user.dto.UserResponse;
import dev.atinroy.ledgerly.domain.user.entity.User;
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
