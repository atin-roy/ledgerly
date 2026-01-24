package dev.atinroy.financebackend.mapper;

import dev.atinroy.financebackend.dto.request.PotCreateRequest;
import dev.atinroy.financebackend.dto.response.PotCreateResponse;
import dev.atinroy.financebackend.entity.Pot;
import org.mapstruct.Mapping;

public interface PotMapper {
    @Mapping(target = "potId", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Pot toEntity(PotCreateRequest potCreateRequest);

    PotCreateResponse toResponse(Pot pot);
}
