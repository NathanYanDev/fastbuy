package dev.nathanyan.fastbuy.product.dto;

import dev.nathanyan.fastbuy.shared.entity.CategoryEntity;
import java.io.Serial;
import java.io.Serializable;

public record CategoryNameResponse(String id, String name, String description)
    implements Serializable {
  @Serial private static final long serialVersionUID = 1001L;

  public static CategoryNameResponse from(CategoryEntity category) {
    return new CategoryNameResponse(
        category.getId(), category.getName(), category.getDescription());
  }
}
