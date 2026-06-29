package dev.atinroy.ledgerly.web;

import dev.atinroy.ledgerly.domain.user.entity.User;
import dev.atinroy.ledgerly.domain.user.entity.UserRole;
import dev.atinroy.ledgerly.domain.user.repository.UserRepository;
import dev.atinroy.ledgerly.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
@Transactional
@DisplayName("IDOR Protection Tests")
class IdrProtectionTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;
    private User user1;
    private User user2;
    private String tokenForUser1;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
        user1 = saveUser("alice@test.com", "alice");
        user2 = saveUser("bob@test.com", "bob");
        tokenForUser1 = jwtService.generateAccessToken(user1);
    }

    @Test
    @DisplayName("GET another user's transactions returns 403")
    void shouldForbidAccessToAnotherUsersTransactions() throws Exception {
        mockMvc.perform(get("/api/users/{userId}/transactions", user2.getId())
                .header("Authorization", "Bearer " + tokenForUser1))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET own transactions returns 200")
    void shouldAllowAccessToOwnTransactions() throws Exception {
        mockMvc.perform(get("/api/users/{userId}/transactions", user1.getId())
                .header("Authorization", "Bearer " + tokenForUser1))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET another user's categories returns 403")
    void shouldForbidAccessToAnotherUsersCategories() throws Exception {
        mockMvc.perform(get("/api/users/{userId}/categories", user2.getId())
                .header("Authorization", "Bearer " + tokenForUser1))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET another user's budgets returns 403")
    void shouldForbidAccessToAnotherUsersBudgets() throws Exception {
        mockMvc.perform(get("/api/users/{userId}/budgets", user2.getId())
                .header("Authorization", "Bearer " + tokenForUser1))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET another user's bills returns 403")
    void shouldForbidAccessToAnotherUsersBills() throws Exception {
        mockMvc.perform(get("/api/users/{userId}/bills", user2.getId())
                .header("Authorization", "Bearer " + tokenForUser1))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Request with no token returns 401")
    void shouldReturn401WhenNoToken() throws Exception {
        mockMvc.perform(get("/api/users/{userId}/transactions", user1.getId()))
                .andExpect(status().isUnauthorized());
    }

    private User saveUser(String email, String username) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("password123"));
        user.setRole(UserRole.USER);
        return userRepository.save(user);
    }
}
