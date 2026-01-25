package dev.atinroy.ledgerly.repository;

import dev.atinroy.ledgerly.entity.Party;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PartyRepository extends JpaRepository<Party, Long> {
    Page<Party> findByUser_UserId(Long userId, Pageable pageable);
    Optional<Party> findByUser_UserIdAndNameIgnoreCase(Long userId, String partyName);
}
