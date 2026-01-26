package dev.atinroy.ledgerly.repository;

import dev.atinroy.ledgerly.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long>{
    // full list of transactions of a user
    Page<Transaction> findByUser_Id(
            Long userId,
            Pageable pageable
    );
    // for finding a specific transaction
    Optional<Transaction> findByUser_IdAndId(
            Long userId,
            Long transactionId
    );

    @Modifying
    @Query("UPDATE Transaction t SET t.category.id = :generalId " +
            "WHERE t.user.id = :userId AND t.category.id = :targetId")
    void reassignCategory(
            @Param("userId") Long userId,
            @Param("targetId") Long targetId,
            @Param("generalId") Long generalId
    );
}
