package dev.nathanyan.fastbuy.product.dto;

import dev.nathanyan.fastbuy.shared.entity.ProductImageEntity;
import dev.nathanyan.fastbuy.shared.entity.ProductVariantEntity;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

public record VariantOptionResponse(
    String sku, String name, BigDecimal price, String coverImageUrl, Boolean available)
    implements Serializable {
  @Serial private static final long serialVersionUID = 1001L;

  public static VariantOptionResponse from(ProductVariantEntity variant) {
    String coverUrl =
        variant.getImages().stream()
            .filter(ProductImageEntity::getIsCover)
            .map(ProductImageEntity::getUrl)
            .findFirst()
            .orElse(null);

    return new VariantOptionResponse(
        variant.getSku(),
        variant.getName(),
        variant.getPrice(),
        coverUrl,
        variant.getActive() && variant.getStockQuantity() > 0);
  }
}
