package dev.nathanyan.fastbuy.product.dto;

import dev.nathanyan.fastbuy.shared.entity.ProductEntity;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record ProductBaseResponse(
    String id,
    String name,
    String description,
    boolean active,
    List<CategoryNameResponse> categories)
    implements Serializable {
  @Serial private static final long serialVersionUID = 1001L;

  public static ProductBaseResponse from(ProductEntity product) {
    return new ProductBaseResponse(
        product.getId(),
        product.getName(),
        product.getDescription(),
        product.getIsActive(),
        product.getCategories().stream()
            .map(
                productCategoryEntity ->
                    CategoryNameResponse.from(productCategoryEntity.getCategory()))
            .toList());
  }
}
