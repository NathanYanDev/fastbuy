package dev.nathanyan.fastbuy.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.io.Serial;
import java.io.Serializable;

public record UpdateEmailRequest(
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    String email
) implements Serializable {
  @Serial
  private static final long serialVersionUID = 1001L;
}
