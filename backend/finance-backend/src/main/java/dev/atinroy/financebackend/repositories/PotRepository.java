package dev.atinroy.financebackend.repositories;

import dev.atinroy.financebackend.entity.Pot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PotRepository extends JpaRepository<Pot, Long> {
    Page<Pot> findByUser_UserId(Long userId, Pageable pageable);
    Page<Pot> findByUser_UserIdAndPotName(Long userId, String potName, Pageable pageable);
}
