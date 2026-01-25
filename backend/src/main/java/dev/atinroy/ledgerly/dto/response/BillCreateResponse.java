package dev.atinroy.ledgerly.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BillCreateResponse {
    private Long billId;
    private String name;
    private String status;
    private LocalDateTime dueDate;
}
