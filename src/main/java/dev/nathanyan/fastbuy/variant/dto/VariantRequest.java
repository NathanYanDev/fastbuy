package dev.nathanyan.fastbuy.variant.dto;

import dev.nathanyan.fastbuy.product.dto.DimensionRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record VariantRequest(
    @NotNull(message = "Product ID is required") String productId,
    @NotBlank(message = "Name is required") String name,
    @NotNull(message = "Price is required") BigDecimal price,
    @NotBlank(message = "Currency is required") String currency,
    @NotNull(message = "Stock quantity is required")
        @Min(value = 0, message = "Stock quantity must be greater than or equal to 0")
        Integer stockQuantity,
    @NotNull(message = "Active is required") Boolean isActive,
    @NotNull(message = "Default variant is required") Boolean isDefaultVariant,
    @NotNull(message = "Dimensions is required") DimensionRequest dimensions,
    @NotNull(message = "Details is required") Map<String, String> details,
    @NotNull(message = "Image IDs is required") List<String> imageIds)
    implements Serializable {
  @Serial private static final long serialVersionUID = 1001L;
}
