package dev.nathanyan.fastbuy.dto.auth;

import java.io.Serial;
import java.io.Serializable;

public record AuthResponseDTO(String token, long expiresIn) implements Serializable {
  @Serial
  private static final long serialVersionUID = 1002L;
}
