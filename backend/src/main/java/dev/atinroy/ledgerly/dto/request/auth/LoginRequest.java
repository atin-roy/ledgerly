package dev.atinroy.ledgerly.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Login request payload")
public record LoginRequest(
        @Schema(description = "User email address", example = "user@example.com")
        @NotBlank String email,
        
        @Schema(description = "User password", example = "Password123!")
        @NotBlank String password
) {}

