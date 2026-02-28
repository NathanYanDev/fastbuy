package dev.nathanyan.fastbuy.shared.entity;

import jakarta.persistence.*;
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
@Entity
@Table(name = "PRODUCT_VARIANT")
public class ProductVariantEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private ProductEntity product;

  @Column(nullable = false, unique = true)
  private String sku;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private BigDecimal price;

  @Column(nullable = false)
  private String currency = "BRL";

  @Column(nullable = false)
  private Integer stockQuantity = 0;

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
