package dev.nathanyan.fastbuy.auth.dto;

import dev.nathanyan.fastbuy.shared.dto.address.AddressDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record RegisterRequest (
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    String username,

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    String password,

    @NotBlank(message = "Name is required")
    @Size(min = 2, message = "Name must be at least 2 characters long")
    String name,

    @Valid
    List<AddressDTO> addresses
) implements Serializable {
  @Serial
  private static final long serialVersionUID = 1002L;
}
