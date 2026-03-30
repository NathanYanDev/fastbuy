package dev.nathanyan.fastbuy.product.dto;

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
}
