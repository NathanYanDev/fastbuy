package dev.nathanyan.fastbuy.shared.repository;

import dev.nathanyan.fastbuy.shared.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<CartEntity, String> {
}
