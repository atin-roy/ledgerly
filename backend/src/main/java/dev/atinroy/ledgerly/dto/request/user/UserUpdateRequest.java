package dev.atinroy.ledgerly.dto.request.user;

public record UserUpdateRequest(
        String email,
        String username,
        String password
) {}
