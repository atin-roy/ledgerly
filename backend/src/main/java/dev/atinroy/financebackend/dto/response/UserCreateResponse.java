package dev.atinroy.financebackend.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserCreateResponse {
    private Long userId;
    private String username;
    private String email;
    private LocalDateTime createdAt;
}
