package dev.atinroy.ledgerly.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Authentication response containing tokens and user information")
public record AuthResponse(
        @Schema(description = "JWT access token for API authorization", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String accessToken,
        
        @Schema(description = "JWT refresh token for obtaining new access tokens")
        String refreshToken,
        
        @Schema(description = "User ID", example = "1")
        Long userId,
        
        @Schema(description = "User email address", example = "user@example.com")
        String email,
        
        @Schema(description = "Username", example = "johndoe")
        String username,
        
        @Schema(description = "User role", example = "USER")
        String role
) {}
