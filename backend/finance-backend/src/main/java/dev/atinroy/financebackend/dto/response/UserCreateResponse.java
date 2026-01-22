package dev.atinroy.financebackend.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class UserCreateResponse {
    private String id;
    private String email;
    private LocalDateTime createdAt;
}
