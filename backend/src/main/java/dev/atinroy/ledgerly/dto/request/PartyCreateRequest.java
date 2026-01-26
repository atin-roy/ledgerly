package dev.atinroy.ledgerly.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PartyCreateRequest(
    @NotBlank String name
) {}
