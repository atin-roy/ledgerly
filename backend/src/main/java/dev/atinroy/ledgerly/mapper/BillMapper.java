package dev.atinroy.ledgerly.mapper;

import dev.atinroy.ledgerly.dto.request.BillCreateRequest;
import dev.atinroy.ledgerly.dto.response.BillCreateResponse;
import dev.atinroy.ledgerly.entity.Bill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BillMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    Bill toEntity(BillCreateRequest billCreateRequest);
    BillCreateResponse toResponse(Bill bill);
}
