package dev.nathanyan.fastbuy.category.dto;

import dev.nathanyan.fastbuy.shared.entity.CategoryEntity;
import java.io.Serial;
import java.io.Serializable;

public record CategoryNameResponse(String id, String name) implements Serializable {
  @Serial private static final long serialVersionUID = 1001L;

  public static CategoryNameResponse from(CategoryEntity category) {
    return new CategoryNameResponse(category.getId(), category.getName());
  }
}
