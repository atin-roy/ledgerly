package dev.atinroy.financebackend.mapper;

import dev.atinroy.financebackend.dto.request.PartyCreateRequest;
import dev.atinroy.financebackend.dto.response.PartyCreateResponse;
import dev.atinroy.financebackend.entity.Party;
import org.mapstruct.Mapping;

public interface PartyMapper {
    @Mapping(target = "partyId", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Party toEntity(PartyCreateRequest partyCreateRequest);

    PartyCreateResponse toResponse(Party party);
}
