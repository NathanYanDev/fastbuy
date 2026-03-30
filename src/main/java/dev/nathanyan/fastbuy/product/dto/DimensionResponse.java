package dev.nathanyan.fastbuy.product.dto;

import dev.nathanyan.fastbuy.shared.entity.ProductDimensionEntity;
import java.io.Serial;
import java.io.Serializable;

public record DimensionResponse(Double width, Double height, Double depth, Double weight)
    implements Serializable {
  @Serial private static final long serialVersionUID = 1001L;

  public static DimensionResponse from(ProductDimensionEntity dimension) {
    return new DimensionResponse(
        dimension.getWidth(), dimension.getHeight(), dimension.getDepth(), dimension.getWeight());
  }
}
