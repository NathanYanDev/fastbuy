package dev.nathanyan.fastbuy.variant.dto;

import dev.nathanyan.fastbuy.product.dto.DimensionResponse;
import dev.nathanyan.fastbuy.shared.entity.ProductImageEntity;
import dev.nathanyan.fastbuy.shared.entity.ProductVariantEntity;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public record VariantDetailResponse(
    String id,
    String sku,
    String name,
    BigDecimal price,
    String currency,
    Integer stockQuantity,
    Boolean available,
    String coverImageUrl,
    List<String> imageUrls,
    DimensionResponse dimension,
    Map<String, String> details)
    implements Serializable {
  @Serial private static final long serialVersionUID = 1001L;

  public static VariantDetailResponse from(ProductVariantEntity variant) {
    String coverUrl =
        variant.getImages().stream()
            .filter(ProductImageEntity::getIsCover)
            .findFirst()
            .map(ProductImageEntity::getUrl)
            .orElse(variant.getImages().get(0).getUrl());
    List<String> gallery =
        variant.getImages().stream()
            .filter(img -> !img.getIsCover())
            .sorted(Comparator.comparingInt(ProductImageEntity::getDisplayOrder))
            .map(ProductImageEntity::getUrl)
            .toList();

    return new VariantDetailResponse(
        variant.getId(),
        variant.getSku(),
        variant.getName(),
        variant.getPrice(),
        variant.getCurrency(),
        variant.getStockQuantity(),
        variant.getIsActive() && variant.getStockQuantity() > 0,
        coverUrl,
        gallery,
        DimensionResponse.from(variant.getDimensions()),
        variant.getDetails() != null ? variant.getDetails() : Map.of());
  }
}
