package dev.atinroy.ledgerly.domain.user.service;

import dev.atinroy.ledgerly.domain.auth.exception.InvalidCredentialsException;
import dev.atinroy.ledgerly.domain.bill.repository.BillRepository;
import dev.atinroy.ledgerly.domain.budget.repository.BudgetRepository;
import dev.atinroy.ledgerly.domain.category.repository.CategoryRepository;
import dev.atinroy.ledgerly.domain.party.repository.PartyRepository;
import dev.atinroy.ledgerly.domain.pot.repository.PotRepository;
import dev.atinroy.ledgerly.domain.transaction.repository.TransactionRepository;
import dev.atinroy.ledgerly.domain.user.dto.AccountDeleteRequest;
import dev.atinroy.ledgerly.domain.user.dto.PasswordChangeRequest;
import dev.atinroy.ledgerly.domain.user.entity.User;
import dev.atinroy.ledgerly.domain.user.mapper.UserMapper;
import dev.atinroy.ledgerly.domain.user.repository.UserRepository;
import dev.atinroy.ledgerly.domain.user.validator.UserValidator;
import dev.atinroy.ledgerly.shared.error.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServicePasswordTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private TransactionRepository transactionRepository;
    @Mock private BillRepository billRepository;
    @Mock private BudgetRepository budgetRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private PartyRepository partyRepository;
    @Mock private PotRepository potRepository;

    @Spy private UserValidator userValidator = new UserValidator();

    @InjectMocks private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setPassword("hashed-old");
    }

    @Test
    @DisplayName("changePassword rejects a wrong current password")
    void changePasswordRejectsWrongCurrent() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed-old")).thenReturn(false);

        assertThatThrownBy(() ->
                userService.changePassword(1L, new PasswordChangeRequest("wrong", "new-password-1")))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("changePassword rejects a too-short new password")
    void changePasswordRejectsWeakNewPassword() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old-password", "hashed-old")).thenReturn(true);

        assertThatThrownBy(() ->
                userService.changePassword(1L, new PasswordChangeRequest("old-password", "short")))
                .isInstanceOf(ValidationException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("changePassword stores the newly encoded password")
    void changePasswordStoresEncoded() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old-password", "hashed-old")).thenReturn(true);
        when(passwordEncoder.encode("new-password-1")).thenReturn("hashed-new");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        userService.changePassword(1L, new PasswordChangeRequest("old-password", "new-password-1"));

        assertThat(user.getPassword()).isEqualTo("hashed-new");
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("deleteAccount refuses without the correct current password")
    void deleteAccountRejectsWrongPassword() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed-old")).thenReturn(false);

        assertThatThrownBy(() ->
                userService.deleteAccount(1L, new AccountDeleteRequest("wrong")))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(userRepository, never()).delete(any());
    }

    @Test
    @DisplayName("deleteAccount removes the user and their data with the correct password")
    void deleteAccountDeletesWithCorrectPassword() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old-password", "hashed-old")).thenReturn(true);

        userService.deleteAccount(1L, new AccountDeleteRequest("old-password"));

        verify(transactionRepository).deleteByUser_Id(1L);
        verify(userRepository).delete(user);
    }
}
