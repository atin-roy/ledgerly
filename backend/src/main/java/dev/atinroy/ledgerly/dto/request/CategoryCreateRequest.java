package dev.atinroy.ledgerly.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CategoryCreateRequest(
    @NotBlank String name
) {}
