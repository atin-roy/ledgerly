package dev.atinroy.ledgerly.dto.request;

import jakarta.validation.constraints.NotNull;

public record PartyUpdateRequest(
    @NotNull Long id,
    String name
) {}
