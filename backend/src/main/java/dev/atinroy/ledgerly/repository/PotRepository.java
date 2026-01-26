package dev.atinroy.ledgerly.repository;

import dev.atinroy.ledgerly.entity.Pot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PotRepository extends JpaRepository<Pot, Long> {
    // Get all pots (no pagination needed)
    List<Pot> findByUser_Id(Long userId);

    // Get a specific pot by ID (for updates/deletes)
    Optional<Pot> findByUser_IdAndId(Long userId, Long potId);
}
