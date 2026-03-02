package dev.nathanyan.fastbuy.auth.dto;

import java.io.Serial;
import java.io.Serializable;

public record AuthResponse(String token, long expiresIn) implements Serializable {
  @Serial
  private static final long serialVersionUID = 1002L;
}
