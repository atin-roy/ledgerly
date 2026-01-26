package dev.atinroy.ledgerly.service;

import dev.atinroy.ledgerly.dto.request.user.UserCreateRequest;
import dev.atinroy.ledgerly.dto.request.user.UserUpdateRequest;
import dev.atinroy.ledgerly.dto.response.UserResponse;
import dev.atinroy.ledgerly.entity.User;
import dev.atinroy.ledgerly.error.ErrorCode;
import dev.atinroy.ledgerly.error.UserNotFoundException;
import dev.atinroy.ledgerly.error.ValidationException;
import dev.atinroy.ledgerly.error.ValidationResult;
import dev.atinroy.ledgerly.mapper.UserMapper;
import dev.atinroy.ledgerly.repository.UserRepository;
import dev.atinroy.ledgerly.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;

    public UserResponse createUser(UserCreateRequest userCreateRequest) {
        User user = userMapper.toEntity(userCreateRequest);
        ValidationResult result = userValidator.validate(user);

        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            result.addFieldError(
                    "email",
                    ErrorCode.ALREADY_EXISTS,
                    "Email already exists"
            );
        }

        if (user.getUsername() != null && userRepository.existsByUsername(user.getUsername())) {
            result.addFieldError(
                    "username",
                    ErrorCode.ALREADY_EXISTS,
                    "Username already exists"
            );
        }

        if (result.hasErrors()) {
            throw new ValidationException(result);
        }

        hashPassword(user);
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    public UserResponse updateUser(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        boolean passwordUpdated = false;
        ValidationResult result = new ValidationResult();

        if (request.email() != null &&
                !request.email().equals(user.getEmail()) &&
                userRepository.existsByEmail(request.email())) {

            result.addFieldError(
                    "email",
                    ErrorCode.ALREADY_EXISTS,
                    "Email already exists"
            );
        }

        if (request.username() != null &&
                !request.username().equals(user.getUsername()) &&
                userRepository.existsByUsername(request.username())) {

            result.addFieldError(
                    "username",
                    ErrorCode.ALREADY_EXISTS,
                    "Username already exists"
            );
        }

        if (request.email() != null) {
            user.setEmail(request.email());
        }

        if (request.username() != null) {
            user.setUsername(request.username());
        }

        if (request.password() != null) {
            user.setPassword(request.password());
            passwordUpdated = true;
        }

        result.merge(userValidator.validate(user));

        if (result.hasErrors()) {
            throw new ValidationException(result);
        }

        if (passwordUpdated) {
            hashPassword(user);
        }

        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        userRepository.delete(user);
    }

    private void hashPassword(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }
}
