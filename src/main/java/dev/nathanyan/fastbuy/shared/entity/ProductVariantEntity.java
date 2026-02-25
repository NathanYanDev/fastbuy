package dev.nathanyan.fastbuy.shared.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity(name = "product_variant")
@Table(name = "product_variants")
public class ProductVariantEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private ProductEntity product;

  @NotBlank
  @Column(nullable = false, unique = true)
  private String sku;

  @NotBlank
  @Column(nullable = false)
  private String name;

  @NotNull
  @Column(nullable = false)
  private BigDecimal price;

  @NotBlank
  @Column(nullable = false)
  private String currency = "BRL";

  @PositiveOrZero
  @Column(nullable = false)
  private Integer stockQuantity = 0;

  @NotNull
  @Column(nullable = false)
  private Boolean active = true;

  @OneToOne(mappedBy = "variant", cascade = CascadeType.ALL, orphanRemoval = true)
  private ProductDimensionEntity dimensions;

  @OneToMany(mappedBy = "variant", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<VariantDetailEntity> details = new ArrayList<>();

  @OneToMany(mappedBy = "variant", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ProductImageEntity> images = new ArrayList<>();

  @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
  private Instant createdAt;

  @Column(nullable = false, columnDefinition = "TIMESTAMP")
  private Instant updatedAt;

  @PrePersist
  protected void onCreate() {
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = Instant.now();
  }
}
