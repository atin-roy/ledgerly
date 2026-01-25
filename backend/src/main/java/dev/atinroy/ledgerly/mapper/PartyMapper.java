package dev.atinroy.ledgerly.mapper;

import dev.atinroy.ledgerly.dto.request.PartyCreateRequest;
import dev.atinroy.ledgerly.dto.response.PartyCreateResponse;
import dev.atinroy.ledgerly.entity.Party;
import org.mapstruct.Mapping;

public interface PartyMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Party toEntity(PartyCreateRequest partyCreateRequest);

    PartyCreateResponse toResponse(Party party);
}
