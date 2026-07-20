package dev.atinroy.ledgerly.domain.user.service;

import dev.atinroy.ledgerly.domain.bill.repository.BillRepository;
import dev.atinroy.ledgerly.domain.budget.repository.BudgetRepository;
import dev.atinroy.ledgerly.domain.party.repository.PartyRepository;
import dev.atinroy.ledgerly.domain.pot.repository.PotRepository;
import dev.atinroy.ledgerly.domain.auth.exception.InvalidCredentialsException;
import dev.atinroy.ledgerly.domain.user.dto.AccountDeleteRequest;
import dev.atinroy.ledgerly.domain.user.dto.PasswordChangeRequest;
import dev.atinroy.ledgerly.domain.user.dto.UserCreateRequest;
import dev.atinroy.ledgerly.domain.user.dto.UserUpdateRequest;
import dev.atinroy.ledgerly.domain.user.dto.UserResponse;
import dev.atinroy.ledgerly.domain.user.entity.User;
import dev.atinroy.ledgerly.shared.error.ErrorCode;
import dev.atinroy.ledgerly.domain.user.exception.UserNotFoundException;
import dev.atinroy.ledgerly.shared.error.ValidationException;
import dev.atinroy.ledgerly.shared.error.ValidationResult;
import dev.atinroy.ledgerly.domain.user.mapper.UserMapper;
import dev.atinroy.ledgerly.domain.category.repository.CategoryRepository;
import dev.atinroy.ledgerly.domain.transaction.repository.TransactionRepository;
import dev.atinroy.ledgerly.domain.user.repository.UserRepository;
import dev.atinroy.ledgerly.domain.user.validator.UserValidator;
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
    public void changePassword(Long userId, PasswordChangeRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        ValidationResult result = userValidator.validateNewPassword(request.newPassword());
        if (result.hasErrors()) {
            throw new ValidationException(result);
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void deleteAccount(Long userId, AccountDeleteRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        deleteUser(userId);
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
