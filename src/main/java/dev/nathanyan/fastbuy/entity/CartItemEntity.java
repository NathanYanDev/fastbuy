package dev.nathanyan.fastbuy.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "cart_items")
public class CartItemEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cart_id", nullable = false)
  private CartEntity cart;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private ProductEntity product;

  @Positive
  @Column(nullable = false)
  private int quantity;

  @Column(nullable = false)
  private Instant addedAt;

  @PrePersist
  protected void onCreate() {
    this.addedAt = Instant.now();
  }
}
