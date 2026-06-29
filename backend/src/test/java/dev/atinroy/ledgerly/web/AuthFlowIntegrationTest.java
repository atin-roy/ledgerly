package dev.atinroy.ledgerly.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import dev.atinroy.ledgerly.domain.auth.dto.RefreshTokenRequest;
import dev.atinroy.ledgerly.domain.auth.dto.AuthResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
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
@DisplayName("Auth Flow Integration Tests")
class AuthFlowIntegrationTest {

    @Autowired
    private WebApplicationContext wac;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("register → access token → protected endpoint returns 200")
    void shouldRegisterAndAccessProtectedEndpoint() throws Exception {
        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email":"flow1@test.com","username":"flowuser1","password":"password123"}
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.userId").isNumber())
                .andReturn();

        AuthResponse auth = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(), AuthResponse.class);

        mockMvc.perform(get("/api/users/{userId}/transactions", auth.userId())
                .header("Authorization", "Bearer " + auth.accessToken()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("register → refresh token → new access token → protected endpoint returns 200")
    void shouldRefreshTokenAndAccessProtectedEndpoint() throws Exception {
        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email":"flow2@test.com","username":"flowuser2","password":"password123"}
                        """))
                .andExpect(status().isCreated())
                .andReturn();

        AuthResponse initial = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(), AuthResponse.class);

        MvcResult refreshResult = mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RefreshTokenRequest(initial.refreshToken()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andReturn();

        AuthResponse refreshed = objectMapper.readValue(
                refreshResult.getResponse().getContentAsString(), AuthResponse.class);

        assertThat(refreshed.accessToken()).isNotBlank();

        mockMvc.perform(get("/api/users/{userId}/transactions", initial.userId())
                .header("Authorization", "Bearer " + refreshed.accessToken()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("protected endpoint with no token returns 401")
    void shouldReturn401WithNoToken() throws Exception {
        mockMvc.perform(get("/api/users/1/transactions"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("login → access token → protected endpoint returns 200")
    void shouldLoginAndAccessProtectedEndpoint() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email":"flow3@test.com","username":"flowuser3","password":"password123"}
                        """))
                .andExpect(status().isCreated());

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email":"flow3@test.com","password":"password123"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andReturn();

        AuthResponse auth = objectMapper.readValue(
                loginResult.getResponse().getContentAsString(), AuthResponse.class);

        mockMvc.perform(get("/api/users/{userId}/transactions", auth.userId())
                .header("Authorization", "Bearer " + auth.accessToken()))
                .andExpect(status().isOk());
    }
}
