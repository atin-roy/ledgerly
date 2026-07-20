package dev.atinroy.ledgerly.domain.user.dto;

import jakarta.validation.constraints.NotBlank;

public record AccountDeleteRequest(
        @NotBlank String currentPassword
) {}
