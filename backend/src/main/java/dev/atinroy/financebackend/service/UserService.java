package dev.atinroy.financebackend.service;

import dev.atinroy.financebackend.entity.User;
import dev.atinroy.financebackend.exception.UserNotFoundException;
import dev.atinroy.financebackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getByEmail(String email) {
        return userRepository
                .findByEmail(normalizeEmail(email))
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
