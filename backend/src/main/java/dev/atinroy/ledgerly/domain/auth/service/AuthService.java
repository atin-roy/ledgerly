package dev.atinroy.ledgerly.domain.auth.service;

import dev.atinroy.ledgerly.domain.auth.dto.LoginRequest;
import dev.atinroy.ledgerly.domain.auth.dto.RefreshTokenRequest;
import dev.atinroy.ledgerly.domain.auth.dto.RegisterRequest;
import dev.atinroy.ledgerly.domain.auth.dto.AuthResponse;
import dev.atinroy.ledgerly.domain.category.entity.Category;
import dev.atinroy.ledgerly.domain.user.entity.User;
import dev.atinroy.ledgerly.domain.user.entity.UserRole;
import dev.atinroy.ledgerly.shared.error.ErrorCode;
import dev.atinroy.ledgerly.domain.auth.exception.InvalidCredentialsException;
import dev.atinroy.ledgerly.domain.auth.exception.InvalidTokenException;
import dev.atinroy.ledgerly.shared.error.ValidationException;
import dev.atinroy.ledgerly.shared.error.ValidationResult;
import dev.atinroy.ledgerly.domain.category.repository.CategoryRepository;
import dev.atinroy.ledgerly.domain.user.repository.UserRepository;
import dev.atinroy.ledgerly.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        ValidationResult result = ValidationResult.withErrors();
        String normalizedEmail = request.email().trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            result.addFieldError("email", ErrorCode.ALREADY_EXISTS, "Email already exists");
        }

        if (userRepository.existsByUsername(request.username())) {
            result.addFieldError("username", ErrorCode.ALREADY_EXISTS, "Username already exists");
        }

        if (result.hasErrors()) {
            throw new ValidationException(result);
        }

        User user = new User();
        user.setEmail(normalizedEmail);
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.USER);

        User savedUser = userRepository.save(user);

        // Create default "General" category for new user
        Category generalCategory = new Category();
        generalCategory.setName("General");
        generalCategory.setUser(savedUser);
        categoryRepository.save(generalCategory);

        return buildAuthResponse(savedUser);
    }

    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(normalizedEmail, request.password())
            );
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        return buildAuthResponse(user);
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        if (!jwtService.isTokenValid(request.refreshToken()) || !jwtService.isRefreshToken(request.refreshToken())) {
            throw new InvalidTokenException("Invalid or expired refresh token");
        }

        Long userId = jwtService.extractUserId(request.refreshToken());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidTokenException("User not found"));

        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(
                accessToken,
                refreshToken,
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getRole().name()
        );
    }
}
