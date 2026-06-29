package dev.atinroy.ledgerly.domain.user.dto;

public record UserUpdateRequest(
        String email,
        String username,
        String password
) {}
