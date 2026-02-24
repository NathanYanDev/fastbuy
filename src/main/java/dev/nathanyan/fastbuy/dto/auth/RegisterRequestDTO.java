package dev.nathanyan.fastbuy.dto.auth;

import dev.nathanyan.fastbuy.dto.address.AddressDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record RegisterRequestDTO (
    String username,
    String password,
    String name,
    List<AddressDTO> addresses
) implements Serializable {
  @Serial
  private static final long serialVersionUID = 1002L;
}
