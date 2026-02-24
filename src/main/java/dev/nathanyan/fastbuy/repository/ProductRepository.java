package dev.nathanyan.fastbuy.repository;

import dev.nathanyan.fastbuy.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, String> {
}
