package dev.atinroy.ledgerly.domain.party.mapper;

import dev.atinroy.ledgerly.domain.party.dto.PartyCreateRequest;
import dev.atinroy.ledgerly.domain.party.dto.PartyResponse;
import dev.atinroy.ledgerly.domain.party.entity.Party;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PartyMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Party toEntity(PartyCreateRequest partyCreateRequest);

    PartyResponse toResponse(Party party);
}
