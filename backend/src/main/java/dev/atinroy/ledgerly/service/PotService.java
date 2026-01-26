package dev.atinroy.ledgerly.service;

import dev.atinroy.ledgerly.dto.request.PotCreateRequest;
import dev.atinroy.ledgerly.dto.request.PotUpdateRequest;
import dev.atinroy.ledgerly.dto.response.PotResponse;
import dev.atinroy.ledgerly.entity.Pot;
import dev.atinroy.ledgerly.entity.User;
import dev.atinroy.ledgerly.error.*;
import dev.atinroy.ledgerly.mapper.PotMapper;
import dev.atinroy.ledgerly.repository.PotRepository;
import dev.atinroy.ledgerly.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PotService {
    private final PotRepository potRepository;
    private final UserRepository userRepository;
    private final PotMapper potMapper;

    @Transactional
    public PotResponse createPot(Long userId, PotCreateRequest request) {
        if (request == null) {
            ValidationResult result = ValidationResult.withErrors();
            result.addGeneralError(ErrorCode.REQUIRED, "Request is required");
            throw new ValidationException(result);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Check for duplicate pot name
        if (potRepository.existsByUser_IdAndName(userId, request.name())) {
            ValidationResult result = ValidationResult.withErrors();
            result.addFieldError("name", ErrorCode.ALREADY_EXISTS, "A pot with this name already exists");
            throw new ValidationException(result);
        }

        Pot pot = potMapper.toEntity(request);
        pot.setUser(user);

        Pot saved = potRepository.save(pot);
        return potMapper.toResponse(saved);
    }

    @Transactional
    public PotResponse updatePot(Long userId, Long potId, PotUpdateRequest request) {
        if (request == null) {
            ValidationResult result = ValidationResult.withErrors();
            result.addGeneralError(ErrorCode.REQUIRED, "Request is required");
            throw new ValidationException(result);
        }

        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Pot pot = potRepository.findByUser_IdAndId(userId, potId)
                .orElseThrow(() -> new PotNotFoundException(potId));

        if (request.name() != null && !request.name().equals(pot.getName())) {
            // Check for duplicate pot name when changing name
            if (potRepository.existsByUser_IdAndName(userId, request.name())) {
                ValidationResult result = ValidationResult.withErrors();
                result.addFieldError("name", ErrorCode.ALREADY_EXISTS, "A pot with this name already exists");
                throw new ValidationException(result);
            }
            pot.setName(request.name());
        }
        if (request.target() != null) {
            pot.setTarget(request.target());
        }
        if (request.saved() != null) {
            pot.setSaved(request.saved());
        }

        Pot saved = potRepository.save(pot);
        return potMapper.toResponse(saved);
    }

    @Transactional
    public void deletePot(Long userId, Long potId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Pot pot = potRepository.findByUser_IdAndId(userId, potId)
                .orElseThrow(() -> new PotNotFoundException(potId));

        potRepository.delete(pot);
    }

    public PotResponse getPot(Long userId, Long potId) {
        Pot pot = potRepository.findByUser_IdAndId(userId, potId)
                .orElseThrow(() -> new PotNotFoundException(potId));
        return potMapper.toResponse(pot);
    }

    public List<PotResponse> getPots(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<Pot> pots = potRepository.findByUser_Id(userId);
        return pots.stream()
                .map(potMapper::toResponse)
                .toList();
    }
}
