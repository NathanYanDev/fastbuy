package dev.nathanyan.fastbuy.repository;

import dev.nathanyan.fastbuy.entity.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<AddressEntity, String> {
}
