package dev.atinroy.ledgerly.dto.response;

public record UserResponse(
        Long id,
        String username,
        String email,
        String role
) {}

