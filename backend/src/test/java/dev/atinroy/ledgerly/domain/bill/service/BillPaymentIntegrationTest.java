package dev.atinroy.ledgerly.domain.bill.service;

import dev.atinroy.ledgerly.domain.bill.dto.BillPayRequest;
import dev.atinroy.ledgerly.domain.bill.entity.Bill;
import dev.atinroy.ledgerly.domain.bill.entity.BillStatus;
import dev.atinroy.ledgerly.domain.bill.repository.BillRepository;
import dev.atinroy.ledgerly.domain.category.entity.Category;
import dev.atinroy.ledgerly.domain.category.repository.CategoryRepository;
import dev.atinroy.ledgerly.domain.transaction.repository.TransactionRepository;
import dev.atinroy.ledgerly.domain.user.entity.User;
import dev.atinroy.ledgerly.domain.user.entity.UserRole;
import dev.atinroy.ledgerly.domain.user.repository.UserRepository;
import dev.atinroy.ledgerly.shared.error.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

// Not @Transactional: each payBill call must commit or roll back on its own
@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=PostgreSQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "jwt.secret=test-secret-key-for-jwt-testing-must-be-at-least-32-bytes-long",
        "jwt.access-token-expiration=3600000",
        "jwt.refresh-token-expiration=86400000"
})
@DisplayName("Bill payment atomicity")
class BillPaymentIntegrationTest {

    @Autowired private BillService billService;
    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private TransactionRepository transactionRepository;
    @MockitoSpyBean private BillRepository billRepository;

    private User user;
    private Category category;
    private Bill bill;

    @BeforeEach
    void setUp() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        user = new User();
        user.setEmail("pay-" + suffix + "@test.com");
        user.setUsername("pay" + suffix);
        user.setPassword("hashed");
        user.setRole(UserRole.USER);
        user = userRepository.save(user);

        category = new Category();
        category.setName("Utilities");
        category.setUser(user);
        category = categoryRepository.save(category);

        bill = new Bill();
        bill.setName("Electricity");
        bill.setAmount(new BigDecimal("1200.00"));
        bill.setDueDate(LocalDateTime.now().plusDays(3));
        bill.setStatus(BillStatus.PENDING);
        bill.setUser(user);
        bill = billRepository.save(bill);
    }

    @Test
    @DisplayName("records one expense and marks the bill paid")
    void paysBill() {
        billService.payBill(user.getId(), bill.getId(), new BillPayRequest(category.getId(), LocalDateTime.now()));

        assertThat(billRepository.findById(bill.getId()).orElseThrow().getStatus()).isEqualTo(BillStatus.PAID);
        assertThat(transactionRepository.findByUser_Id(user.getId()))
                .singleElement()
                .satisfies(t -> assertThat(t.getAmount()).isEqualByComparingTo("-1200.00"));
    }

    @Test
    @DisplayName("a failed status update rolls back the expense")
    void rollsBackExpenseWhenStatusUpdateFails() {
        doThrow(new RuntimeException("simulated failure")).when(billRepository).save(any(Bill.class));

        assertThatThrownBy(() -> billService.payBill(
                user.getId(), bill.getId(), new BillPayRequest(category.getId(), LocalDateTime.now())))
                .hasMessage("simulated failure");

        assertThat(transactionRepository.findByUser_Id(user.getId())).isEmpty();
        assertThat(billRepository.findById(bill.getId()).orElseThrow().getStatus()).isEqualTo(BillStatus.PENDING);
    }

    @Test
    @DisplayName("paying twice is rejected without a second expense")
    void rejectsSecondPayment() {
        BillPayRequest request = new BillPayRequest(category.getId(), LocalDateTime.now());
        billService.payBill(user.getId(), bill.getId(), request);

        assertThatThrownBy(() -> billService.payBill(user.getId(), bill.getId(), request))
                .isInstanceOf(ValidationException.class);
        assertThat(transactionRepository.findByUser_Id(user.getId())).hasSize(1);
    }
}
