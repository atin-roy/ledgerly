package dev.atinroy.ledgerly.repository;

import dev.atinroy.ledgerly.entity.Party;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PartyRepository extends JpaRepository<Party, Long> {

    // Autocomplete search - searches against normalized name
    // This way "kfc", "KFC", "Kfc" all match the same party
    @Query("SELECT p FROM Party p WHERE p.user.id = :userId " +
            "AND LOWER(p.normalizedName) LIKE LOWER(CONCAT(:searchTerm, '%'))")
    List<Party> searchByNameStartingWith(
            @Param("userId") Long userId,
            @Param("searchTerm") String searchTerm
    );

    // Find by exact normalized name - for deduplication
    // Used when creating/finding a party to check if it exists
    Optional<Party> findByUser_IdAndNormalizedName(Long userId, String normalizedName);

    // Find by ID - for security checks
    // This one doesn't care about names at all
    Optional<Party> findByUser_IdAndId(Long userId, Long partyId);
}
