package dev.atinroy.ledgerly.dto.request.user;

import jakarta.validation.constraints.NotBlank;

public record UserCreateRequest(
        @NotBlank String email,
        @NotBlank String username,
        @NotBlank String password
) {}
