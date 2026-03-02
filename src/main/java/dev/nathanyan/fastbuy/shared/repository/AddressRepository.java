package dev.nathanyan.fastbuy.shared.repository;

import dev.nathanyan.fastbuy.shared.entity.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<AddressEntity, String> {
}
