package dev.atinroy.financebackend.repository;

import dev.atinroy.financebackend.entity.Party;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyRepository extends JpaRepository<Party, Long> {
    Page<Party> findByUser_UserId(Long userId, Pageable pageable);
    Page<Party> findByUser_UserIdAndPartyNameIgnoreCase(Long userId, String partyName, Pageable pageable);
}
