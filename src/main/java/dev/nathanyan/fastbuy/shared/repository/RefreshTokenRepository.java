package dev.nathanyan.fastbuy.shared.repository;

import dev.nathanyan.fastbuy.shared.entity.CustomerEntity;
import dev.nathanyan.fastbuy.shared.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, String> {
  boolean existsByTokenAndCustomer_Email(String token, String email);
  void deleteByCustomer(CustomerEntity customer);
}
