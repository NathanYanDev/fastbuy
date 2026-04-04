package dev.nathanyan.fastbuy.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record ProductRequest(
    @NotBlank(message = "Name cannot be blank") String name,
    String description,
    @NotNull(message = "Categories cannot be null") List<String> categoryIds) implements Serializable {
  @Serial
  private static final long serialVersionUID = 1001L;
}
