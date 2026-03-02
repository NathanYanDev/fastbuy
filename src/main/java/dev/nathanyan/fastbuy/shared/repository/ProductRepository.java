package dev.nathanyan.fastbuy.shared.repository;

import dev.nathanyan.fastbuy.shared.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, String> {
}
