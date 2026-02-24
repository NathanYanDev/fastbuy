package dev.nathanyan.fastbuy.repository;

import dev.nathanyan.fastbuy.entity.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItemEntity, String> {
}
