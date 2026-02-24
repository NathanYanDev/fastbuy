package dev.nathanyan.fastbuy.repository;

import dev.nathanyan.fastbuy.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, String> {
}
