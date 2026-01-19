package dev.atinroy.financebackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long potId;

    @Column(nullable = false, length = 100)
    private String potName;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal potTarget;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal potSaved;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
