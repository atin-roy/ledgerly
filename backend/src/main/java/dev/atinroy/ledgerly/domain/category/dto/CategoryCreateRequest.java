package dev.atinroy.ledgerly.domain.category.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryCreateRequest(
    @NotBlank String name
) {}
