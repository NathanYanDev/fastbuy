package dev.nathanyan.fastbuy.shared.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "PRODUCT_DIMENSION")
public class ProductDimensionEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "variant_id")
  private ProductVariantEntity variant;

  @Column(nullable = false)
  private Double height;

  @Column(nullable = false)
  private Double width;

  @Column(nullable = false)
  private Double depth;

  @Column(nullable = false)
  private Double weight;

  @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
  private Instant createdAt;

  @PrePersist
  protected void onCreate() {
    this.createdAt = Instant.now();
  }
}
