package dev.nathanyan.fastbuy.product.dto;

import dev.nathanyan.fastbuy.category.dto.CategoryNameResponse;
import dev.nathanyan.fastbuy.shared.entity.ProductEntity;
import dev.nathanyan.fastbuy.shared.entity.ProductVariantEntity;
import dev.nathanyan.fastbuy.variant.dto.VariantOptionResponse;
import dev.nathanyan.fastbuy.variant.dto.VariantSummaryResponse;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record ProductSummaryResponse(
    String id,
    String name,
    List<CategoryNameResponse> categories,
    VariantSummaryResponse defaultVariant,
    List<VariantOptionResponse> othersVariants)
    implements Serializable {
  @Serial private static final long serialVersionUID = 1001L;

  public static ProductSummaryResponse from(ProductEntity product) {
    ProductVariantEntity defaultVariant =
        product.getVariants().stream()
            .filter(ProductVariantEntity::getDefaultVariant)
            .findFirst()
            .orElse(product.getVariants().get(0));
    List<VariantOptionResponse> othersVariants =
        product.getVariants().stream()
            .filter(v -> !v.getDefaultVariant())
            .map(VariantOptionResponse::from)
            .toList();

    return new ProductSummaryResponse(
        product.getId(),
        product.getName(),
        product.getCategories().stream()
            .map(pc -> CategoryNameResponse.from(pc.getCategory()))
            .toList(),
        VariantSummaryResponse.from(defaultVariant),
        othersVariants);
  }
}
