package dev.atinroy.ledgerly.service;

import dev.atinroy.ledgerly.dto.request.UserCreateRequest;
import dev.atinroy.ledgerly.dto.response.UserResponse;
import dev.atinroy.ledgerly.mapper.UserMapper;
import dev.atinroy.ledgerly.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
}
