package dev.atinroy.ledgerly.service;

import dev.atinroy.ledgerly.entity.User;
import dev.atinroy.ledgerly.exception.UserNotFoundException;
import dev.atinroy.ledgerly.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
}
