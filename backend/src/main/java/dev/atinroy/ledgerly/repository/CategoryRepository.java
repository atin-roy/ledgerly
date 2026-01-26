package dev.atinroy.ledgerly.repository;

import dev.atinroy.ledgerly.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    // For loading dropdown options
    List<Category> findByUser_Id(Long userId);

    // For validating access when creating/updating transactions
    Optional<Category> findByUser_IdAndId(Long userId, Long categoryId);

    // For validating unique names when creating new categories
    boolean existsByUser_IdAndName(Long userId, String name);
}
