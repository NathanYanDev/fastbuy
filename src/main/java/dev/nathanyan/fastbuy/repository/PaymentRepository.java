package dev.nathanyan.fastbuy.repository;

import dev.nathanyan.fastbuy.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentEntity, String> {
}
