package dev.nathanyan.fastbuy.product.dto;

import dev.nathanyan.fastbuy.shared.entity.ProductImageEntity;
import dev.nathanyan.fastbuy.shared.entity.ProductVariantEntity;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

public record VariantSummaryResponse(
    String sku,
    String name,
    BigDecimal price,
    String coverImageUrl,
    Integer stockQuantity,
    Boolean available
) implements Serializable {
  @Serial private static final long serialVersionUID = 1001L;

  public static VariantSummaryResponse from(ProductVariantEntity variant) {
    String coverUrl = variant.getImages().stream()
        .filter(ProductImageEntity::getIsCover)
        .map(ProductImageEntity::getUrl)
        .findFirst()
        .orElse(null);

    return new VariantSummaryResponse(
        variant.getSku(),
        variant.getName(),
        variant.getPrice(),
        coverUrl,
        variant.getStockQuantity(),
        variant.getActive() && variant.getStockQuantity() > 0
    );
  }
}
