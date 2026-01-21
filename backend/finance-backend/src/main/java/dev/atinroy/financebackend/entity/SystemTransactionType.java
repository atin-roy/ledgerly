package dev.atinroy.financebackend.entity;

import jakarta.persistence.*;

@Entity
public class SystemTransactionType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long systemTransactionId;
    @Column(nullable = false, unique = true)
    private String systemTransactionName;
}
