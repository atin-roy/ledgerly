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
import dev.atinroy.ledgerly.repository.*;
import dev.atinroy.ledgerly.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;
    private final TransactionRepository transactionRepository;
    private final BillRepository billRepository;
    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final PartyRepository partyRepository;
    private final PotRepository potRepository;

    @Transactional
    public UserResponse createUser(UserCreateRequest userCreateRequest) {
        ValidationResult result = userValidator.validate(userCreateRequest);

        if (userCreateRequest.email() != null && userRepository.existsByEmail(userCreateRequest.email())) {
            result.addFieldError(
                    "email",
                    ErrorCode.ALREADY_EXISTS,
                    "Email already exists"
            );
        }

        if (userCreateRequest.username() != null && userRepository.existsByUsername(userCreateRequest.username())) {
            result.addFieldError(
                    "username",
                    ErrorCode.ALREADY_EXISTS,
                    "Username already exists"
            );
        }

        if (result.hasErrors()) {
            throw new ValidationException(result);
        }

        User user = userMapper.toEntity(userCreateRequest);
        hashPassword(user);
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Transactional
    public UserResponse updateUser(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        boolean passwordUpdated = false;
        ValidationResult result = userValidator.validate(request);

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

        if (result.hasErrors()) {
            throw new ValidationException(result);
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

        if (passwordUpdated) {
            hashPassword(user);
        }

        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Delete all related data in correct order to handle dependencies
        transactionRepository.deleteByUser_Id(userId);
        billRepository.deleteByUser_Id(userId);
        budgetRepository.deleteByUser_Id(userId);
        categoryRepository.deleteByUser_Id(userId);
        partyRepository.deleteByUser_Id(userId);
        potRepository.deleteByUser_Id(userId);

        // Finally, delete the user
        userRepository.delete(user);
    }

    private void hashPassword(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }
}
