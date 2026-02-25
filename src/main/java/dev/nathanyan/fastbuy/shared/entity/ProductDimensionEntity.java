package dev.nathanyan.fastbuy.shared.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity(name = "product_dimension")
@Table(name = "product_dimensions")
public class ProductDimensionEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "variant_id")
  private ProductVariantEntity variant;

  @PositiveOrZero
  @Column(nullable = false)
  private Double height;

  @PositiveOrZero
  @Column(nullable = false)
  private Double width;

  @PositiveOrZero
  @Column(nullable = false)
  private Double depth;

  @PositiveOrZero
  @Column(nullable = false)
  private Double weight;

  @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
  private Instant createdAt;

  @PrePersist
  protected void onCreate() {
    this.createdAt = Instant.now();
  }
}
