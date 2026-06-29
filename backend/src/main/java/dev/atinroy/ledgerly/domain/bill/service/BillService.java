package dev.atinroy.ledgerly.domain.bill.service;

import dev.atinroy.ledgerly.domain.bill.exception.BillNotFoundException;
import dev.atinroy.ledgerly.domain.user.exception.UserNotFoundException;
import dev.atinroy.ledgerly.domain.bill.dto.BillCreateRequest;
import dev.atinroy.ledgerly.domain.bill.dto.BillUpdateRequest;
import dev.atinroy.ledgerly.domain.bill.dto.BillResponse;
import dev.atinroy.ledgerly.domain.bill.entity.Bill;
import dev.atinroy.ledgerly.domain.user.entity.User;
import dev.atinroy.ledgerly.domain.bill.entity.BillStatus;
import dev.atinroy.ledgerly.shared.error.*;
import dev.atinroy.ledgerly.domain.bill.mapper.BillMapper;
import dev.atinroy.ledgerly.domain.bill.repository.BillRepository;
import dev.atinroy.ledgerly.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BillService {
    private final BillRepository billRepository;
    private final UserRepository userRepository;
    private final BillMapper billMapper;

    @Transactional
    public BillResponse createBill(Long userId, BillCreateRequest request) {
        if (request == null) {
            ValidationResult result = ValidationResult.withErrors();
            result.addGeneralError(ErrorCode.REQUIRED, "Request is required");
            throw new ValidationException(result);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Validate amount is positive
        if (request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            ValidationResult result = ValidationResult.withErrors();
            result.addFieldError("amount", ErrorCode.INVALID_VALUE, "Amount must be positive");
            throw new ValidationException(result);
        }

        Bill bill = billMapper.toEntity(request);
        bill.setUser(user);

        Bill saved = billRepository.save(bill);
        return billMapper.toResponse(saved);
    }

    @Transactional
    public BillResponse updateBill(Long userId, Long billId, BillUpdateRequest request) {
        if (request == null) {
            ValidationResult result = ValidationResult.withErrors();
            result.addGeneralError(ErrorCode.REQUIRED, "Request is required");
            throw new ValidationException(result);
        }

        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Bill bill = billRepository.findByUser_IdAndId(userId, billId)
                .orElseThrow(() -> new BillNotFoundException(billId));

        if (request.name() != null) {
            bill.setName(request.name().trim());
        }

        if (request.amount() != null) {
            if (request.amount().compareTo(BigDecimal.ZERO) <= 0) {
                ValidationResult result = ValidationResult.withErrors();
                result.addFieldError("amount", ErrorCode.INVALID_VALUE, "Amount must be positive");
                throw new ValidationException(result);
            }
            bill.setAmount(request.amount());
        }

        if (request.dueDate() != null) {
            bill.setDueDate(request.dueDate());
        }

        if (request.status() != null) {
            bill.setStatus(request.status());
        }

        Bill saved = billRepository.save(bill);
        return billMapper.toResponse(saved);
    }

    @Transactional
    public void deleteBill(Long userId, Long billId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Bill bill = billRepository.findByUser_IdAndId(userId, billId)
                .orElseThrow(() -> new BillNotFoundException(billId));

        billRepository.delete(bill);
    }

    @Transactional(readOnly = true)
    public BillResponse getBill(Long userId, Long billId) {
        Bill bill = billRepository.findByUser_IdAndId(userId, billId)
                .orElseThrow(() -> new BillNotFoundException(billId));
        return billMapper.toResponse(bill);
    }

    @Transactional(readOnly = true)
    public List<BillResponse> getBills(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<Bill> bills = billRepository.findByUser_Id(userId);
        return bills.stream()
                .map(billMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public long countBillsByStatus(Long userId, BillStatus status) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return billRepository.countByUser_IdAndStatus(userId, status);
    }

    @Transactional(readOnly = true)
    public BigDecimal sumBillsByStatus(Long userId, BillStatus status) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        BigDecimal sum = billRepository.sumAmountByUserIdAndStatus(userId, status);
        return sum != null ? sum : BigDecimal.ZERO;
    }
}
