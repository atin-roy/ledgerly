package dev.atinroy.ledgerly.service;

import dev.atinroy.ledgerly.dto.request.auth.LoginRequest;
import dev.atinroy.ledgerly.dto.request.auth.RefreshTokenRequest;
import dev.atinroy.ledgerly.dto.request.auth.RegisterRequest;
import dev.atinroy.ledgerly.dto.response.AuthResponse;
import dev.atinroy.ledgerly.entity.User;
import dev.atinroy.ledgerly.entity.enums.UserRole;
import dev.atinroy.ledgerly.error.ErrorCode;
import dev.atinroy.ledgerly.error.InvalidCredentialsException;
import dev.atinroy.ledgerly.error.InvalidTokenException;
import dev.atinroy.ledgerly.error.ValidationException;
import dev.atinroy.ledgerly.error.ValidationResult;
import dev.atinroy.ledgerly.repository.UserRepository;
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
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        ValidationResult result = ValidationResult.withErrors();

        if (userRepository.existsByEmail(request.email())) {
            result.addFieldError("email", ErrorCode.ALREADY_EXISTS, "Email already exists");
        }

        if (userRepository.existsByUsername(request.username())) {
            result.addFieldError("username", ErrorCode.ALREADY_EXISTS, "Username already exists");
        }

        if (result.hasErrors()) {
            throw new ValidationException(result);
        }

        User user = new User();
        user.setEmail(request.email());
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.USER);

        User savedUser = userRepository.save(user);

        return buildAuthResponse(savedUser);
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        return buildAuthResponse(user);
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        if (!jwtService.isTokenValid(request.refreshToken())) {
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
