package dev.atinroy.ledgerly.domain.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UserCreateRequest(
        @NotBlank String email,
        @NotBlank String username,
        @NotBlank String password
) {}
