package dev.atinroy.financebackend.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BillCreateResponse {
    private String id;
    private String billName;
    private String billStatus;
    private LocalDateTime billDueDate;
}
