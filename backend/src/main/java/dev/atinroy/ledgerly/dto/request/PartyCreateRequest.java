package dev.atinroy.ledgerly.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartyCreateRequest {
    @NotBlank
    private String name;
}
