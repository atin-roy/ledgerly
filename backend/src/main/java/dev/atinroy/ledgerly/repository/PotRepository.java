package dev.atinroy.ledgerly.repository;

import dev.atinroy.ledgerly.entity.Pot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PotRepository extends JpaRepository<Pot, Long> {
    // Get all pots (no pagination needed)
    List<Pot> findByUser_Id(Long userId);

    // Get a specific pot by ID (for updates/deletes)
    Optional<Pot> findByUser_IdAndId(Long userId, Long potId);

    // Check for duplicate pot name for a user
    boolean existsByUser_IdAndName(Long userId, String name);

    @Modifying
    @Query("DELETE FROM Pot p WHERE p.user.id = :userId")
    void deleteByUser_Id(@Param("userId") Long userId);
}
