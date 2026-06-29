package dev.atinroy.ledgerly.domain.user.dto;

public record UserResponse(
        Long id,
        String username,
        String email,
        String role
) {}

