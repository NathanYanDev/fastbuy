package dev.nathanyan.fastbuy.repository;

import dev.nathanyan.fastbuy.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, String> {
}
