package dev.atinroy.ledgerly.domain.party.dto;

import jakarta.validation.constraints.NotBlank;

public record PartyCreateRequest(
    @NotBlank String name
) {}
