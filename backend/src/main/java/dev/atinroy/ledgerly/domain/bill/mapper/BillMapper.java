package dev.atinroy.ledgerly.domain.bill.mapper;

import dev.atinroy.ledgerly.domain.bill.dto.BillCreateRequest;
import dev.atinroy.ledgerly.domain.bill.dto.BillResponse;
import dev.atinroy.ledgerly.domain.bill.entity.Bill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BillMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    Bill toEntity(BillCreateRequest billCreateRequest);

    BillResponse toResponse(Bill bill);
}
