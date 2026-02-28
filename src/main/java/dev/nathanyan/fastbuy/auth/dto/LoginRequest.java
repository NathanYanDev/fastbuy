package dev.nathanyan.fastbuy.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.io.Serial;
import java.io.Serializable;

public record LoginRequest(
    @Email(message = "Invalid email format")
    @NotBlank(message = "Username is required")
    String username,

    @NotBlank(message = "Password is required")
    String password
) implements Serializable {
  @Serial
  private static final long serialVersionUID = 1002L;
}
