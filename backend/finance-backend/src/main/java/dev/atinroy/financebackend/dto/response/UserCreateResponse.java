package dev.atinroy.financebackend.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserCreateResponse {
    private Long id;
    private String email;
    private LocalDateTime createdAt;
}
