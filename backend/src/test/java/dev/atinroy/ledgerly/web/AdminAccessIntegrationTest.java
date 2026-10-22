package dev.atinroy.ledgerly.web;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.atinroy.ledgerly.domain.auth.dto.AuthResponse;
import dev.atinroy.ledgerly.domain.user.entity.UserRole;
import dev.atinroy.ledgerly.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
@DisplayName("Admin Access Integration Tests")
class AdminAccessIntegrationTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
    }

    private AuthResponse register(String email, String username) throws Exception {
        String body = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","username":"%s","password":"password123"}
                                """.formatted(email, username)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(body, AuthResponse.class);
    }

    @Test
    @DisplayName("regular user gets 403 on the admin user search")
    void shouldForbidRegularUser() throws Exception {
        AuthResponse auth = register("plain@test.com", "plainuser");

        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + auth.accessToken()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("admin can search users by email, without the password hash leaking")
    void shouldLetAdminSearchByEmail() throws Exception {
        AuthResponse admin = register("boss@test.com", "bossuser");
        register("alice@example.org", "aliceuser");
        register("bob@test.com", "bobuser");

        var adminUser = userRepository.findByEmail("boss@test.com").orElseThrow();
        adminUser.setRole(UserRole.ADMIN);
        userRepository.flush();

        // authorities are loaded from the DB per request, so the old token picks up the new role
        mockMvc.perform(get("/api/admin/users").param("q", "EXAMPLE")
                        .header("Authorization", "Bearer " + admin.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].email").value("alice@example.org"))
                .andExpect(jsonPath("$[0].password").doesNotExist());
    }
}
