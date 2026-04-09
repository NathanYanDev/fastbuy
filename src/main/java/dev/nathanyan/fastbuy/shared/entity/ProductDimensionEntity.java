package dev.nathanyan.fastbuy.shared.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import lombok.*;

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

  public ProductDimensionEntity(Double height, Double width, Double depth, Double weight) {
    this.height = height;
    this.width = width;
    this.depth = depth;
    this.weight = weight;
  }

  @PrePersist
  protected void onCreate() {
    this.createdAt = Instant.now();
  }
}
