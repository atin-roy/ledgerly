package dev.atinroy.ledgerly.domain.pot.mapper;

import dev.atinroy.ledgerly.domain.pot.dto.PotCreateRequest;
import dev.atinroy.ledgerly.domain.pot.dto.PotResponse;
import dev.atinroy.ledgerly.domain.pot.entity.Pot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PotMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Pot toEntity(PotCreateRequest potCreateRequest);

    PotResponse toResponse(Pot pot);
}
