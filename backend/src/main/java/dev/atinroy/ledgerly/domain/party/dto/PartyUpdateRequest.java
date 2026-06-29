package dev.atinroy.ledgerly.domain.party.dto;

import jakarta.validation.constraints.NotNull;

public record PartyUpdateRequest(
    @NotNull Long id,
    String name
) {}
