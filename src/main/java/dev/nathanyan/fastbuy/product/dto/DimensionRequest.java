package dev.nathanyan.fastbuy.product.dto;

import dev.nathanyan.fastbuy.shared.entity.ProductDimensionEntity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

public record DimensionRequest(
    @NotNull(message = "Width is required")
        @Min(value = 0, message = "Width must be greater than or equal to 0")
        Double width,
    @NotNull(message = "Height is required")
        @Min(value = 0, message = "Height must be greater than or equal to 0")
        Double height,
    @NotNull(message = "Depth is required")
        @Min(value = 0, message = "Depth must be greater than or equal to 0")
        Double depth,
    @NotNull(message = "Weight is required")
        @Min(value = 0, message = "Weight must be greater than or equal to 0")
        Double weight)
    implements Serializable {
  @Serial private static final long serialVersionUID = 1001L;

  public static ProductDimensionEntity toDimension(DimensionRequest dimensionRequest) {
    return new ProductDimensionEntity(
        dimensionRequest.width(),
        dimensionRequest.height(),
        dimensionRequest.depth(),
        dimensionRequest.weight());
  }
}
