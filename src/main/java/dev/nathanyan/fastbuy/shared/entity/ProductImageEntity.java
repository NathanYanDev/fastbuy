package dev.nathanyan.fastbuy.shared.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity(name = "product_image")
@Table(name = "product_images")
public class ProductImageEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "variant_id", nullable = false)
  private ProductVariantEntity variant;

  @NotBlank
  @Column(nullable = false)
  private String url;

  @NotNull
  @Column(nullable = false)
  private Boolean isCover = false;

  @PositiveOrZero
  @Column(nullable = false)
  private Integer displayOrder = 0;

  @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
  private Instant createdAt;

  @PrePersist
  protected void onCreate() {
    this.createdAt = Instant.now();
  }
}
