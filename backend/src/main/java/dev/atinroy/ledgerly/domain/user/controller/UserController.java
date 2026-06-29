package dev.atinroy.ledgerly.domain.user.controller;

import dev.atinroy.ledgerly.domain.user.dto.UserUpdateRequest;
import dev.atinroy.ledgerly.domain.user.dto.UserResponse;
import dev.atinroy.ledgerly.security.AuthenticatedUserId;
import dev.atinroy.ledgerly.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(
            @AuthenticatedUserId Long authenticatedUserId,
            @PathVariable Long userId,
            @Valid @RequestBody UserUpdateRequest request) {
        if (!authenticatedUserId.equals(userId)) {
            throw new AccessDeniedException("You can only update your own account");
        }
        UserResponse response = userService.updateUser(userId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @AuthenticatedUserId Long authenticatedUserId,
            @PathVariable Long userId) {
        if (!authenticatedUserId.equals(userId)) {
            throw new AccessDeniedException("You can only delete your own account");
        }
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
