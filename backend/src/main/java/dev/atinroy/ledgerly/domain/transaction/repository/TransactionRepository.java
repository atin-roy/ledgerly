package dev.atinroy.ledgerly.domain.transaction.repository;

import dev.atinroy.ledgerly.domain.transaction.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends
        JpaRepository<Transaction, Long>,
        JpaSpecificationExecutor<Transaction> {
    // full list of transactions of a user
    Page<Transaction> findByUser_Id(
            Long userId,
            Pageable pageable
    );
    
    // full list of transactions of a user (unbound - for cascade deletes)
    List<Transaction> findByUser_Id(Long userId);
    
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

    @Modifying
    @Query("DELETE FROM Transaction t WHERE t.user.id = :userId")
    void deleteByUser_Id(@Param("userId") Long userId);

    @Query("SELECT t.category.id, COALESCE(SUM(ABS(t.amount)), 0) FROM Transaction t " +
            "WHERE t.user.id = :userId AND t.amount < 0 GROUP BY t.category.id")
    List<Object[]> sumSpendingByCategoryForUser(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(ABS(t.amount)), 0) FROM Transaction t " +
            "WHERE t.user.id = :userId AND t.category.id = :categoryId AND t.amount < 0")
    BigDecimal sumSpentByCategoryId(@Param("userId") Long userId, @Param("categoryId") Long categoryId);
}
