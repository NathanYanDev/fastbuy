package dev.nathanyan.fastbuy.product.dto;

import jakarta.validation.constraints.NotBlank;
import java.io.Serial;
import java.io.Serializable;

public record CategoryRequest(
    @NotBlank(message = "Id cannot be blank")
    String id,

    @NotBlank(message = "Name cannot be blank")
    String name
) implements Serializable {
  @Serial
  private static final long serialVersionUID = 1001L;
}
