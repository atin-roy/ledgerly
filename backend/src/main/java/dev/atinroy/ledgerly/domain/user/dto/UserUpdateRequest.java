package dev.atinroy.ledgerly.domain.user.dto;

// Password is intentionally excluded — changing it goes through the
// current-password-verified POST /users/{id}/password, never this PUT.
public record UserUpdateRequest(
        String email,
        String username
) {}
