package dev.nathanyan.fastbuy.repository;

import dev.nathanyan.fastbuy.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<CartEntity, String> {
}
