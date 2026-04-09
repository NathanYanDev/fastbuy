package dev.nathanyan.fastbuy.product.dto;

import dev.nathanyan.fastbuy.category.dto.CategoryNameResponse;
import dev.nathanyan.fastbuy.shared.entity.ProductEntity;
import dev.nathanyan.fastbuy.variant.dto.VariantDetailResponse;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record ProductDetailResponse(
    String id,
    String name,
    List<CategoryNameResponse> categories,
    List<VariantDetailResponse> variants)
    implements Serializable {
  @Serial private static final long serialVersionUID = 1001L;

  public static ProductDetailResponse from(ProductEntity product) {
    return new ProductDetailResponse(
        product.getId(),
        product.getName(),
        product.getCategories().stream()
            .map(
                productCategoryEntity ->
                    CategoryNameResponse.from(productCategoryEntity.getCategory()))
            .toList(),
        product.getVariants().stream().map(VariantDetailResponse::from).toList());
  }
}
