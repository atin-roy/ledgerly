package dev.atinroy.ledgerly.dto.request.user;

import jakarta.validation.constraints.NotBlank;

public record UserUpdateRequest(
        String email,
        String username,
        String password
) {}
