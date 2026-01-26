package dev.atinroy.ledgerly.dto.request.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String email,
        @NotBlank String password
) {}

