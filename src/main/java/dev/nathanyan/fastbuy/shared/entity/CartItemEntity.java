package dev.nathanyan.fastbuy.shared.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity(name = "cart_item")
@Table(name = "cart_items")
public class CartItemEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cart_id", nullable = false)
  private CartEntity cart;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "variant_id", nullable = false)
  private ProductVariantEntity variant;

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
