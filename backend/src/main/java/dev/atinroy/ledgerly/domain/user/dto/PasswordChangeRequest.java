package dev.atinroy.ledgerly.domain.user.dto;

import jakarta.validation.constraints.NotBlank;

public record PasswordChangeRequest(
        @NotBlank String currentPassword,
        @NotBlank String newPassword
) {}
