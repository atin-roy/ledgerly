package dev.atinroy.ledgerly.mapper;

import dev.atinroy.ledgerly.dto.request.PotCreateRequest;
import dev.atinroy.ledgerly.dto.response.PotCreateResponse;
import dev.atinroy.ledgerly.entity.Pot;
import org.mapstruct.Mapping;

public interface PotMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Pot toEntity(PotCreateRequest potCreateRequest);

    PotCreateResponse toResponse(Pot pot);
}
