package dev.atinroy.ledgerly.dto.response;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        Long userId,
        String email,
        String username,
        String role
) {}
