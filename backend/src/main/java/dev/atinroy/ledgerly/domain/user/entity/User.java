package dev.atinroy.ledgerly.domain.user.entity;

import dev.atinroy.ledgerly.domain.bill.entity.Bill;
import dev.atinroy.ledgerly.domain.budget.entity.Budget;
import dev.atinroy.ledgerly.domain.category.entity.Category;
import dev.atinroy.ledgerly.domain.party.entity.Party;
import dev.atinroy.ledgerly.domain.pot.entity.Pot;
import dev.atinroy.ledgerly.domain.transaction.entity.Transaction;
import dev.atinroy.ledgerly.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "app_user")
public class User extends BaseEntity {
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Bill> bills;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Budget> budgets;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Category> categories;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Party> parties;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Pot> pots;
}
