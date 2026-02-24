package dev.nathanyan.fastbuy.repository;

import dev.nathanyan.fastbuy.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, String> {
}
