package dev.atinroy.ledgerly.domain.user.controller;

import dev.atinroy.ledgerly.domain.user.dto.UserResponse;
import dev.atinroy.ledgerly.domain.user.mapper.UserMapper;
import dev.atinroy.ledgerly.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// guarded twice: the /api/admin/** rule in SecurityConfig and this annotation
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @GetMapping
    public List<UserResponse> searchUsers(@RequestParam(defaultValue = "") String q) {
        return userRepository.findTop50ByEmailContainingIgnoreCaseOrderByEmail(q.strip()).stream()
                .map(userMapper::toResponse)
                .toList();
    }
}
