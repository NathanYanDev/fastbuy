package dev.nathanyan.fastbuy.shared.repository;

import dev.nathanyan.fastbuy.shared.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<CustomerEntity, String> {
  Optional<CustomerEntity> findByEmail(String email);
}
