package dev.atinroy.financebackend.mapper;

import dev.atinroy.financebackend.dto.request.BillCreateRequest;
import dev.atinroy.financebackend.dto.response.BillCreateResponse;
import dev.atinroy.financebackend.entity.Bill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BillMapper {
    @Mapping(target = "billId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    Bill toEntity(BillCreateRequest billCreateRequest);
    BillCreateResponse toDto(Bill bill);
}
