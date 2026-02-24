package dev.nathanyan.fastbuy.dto.auth;

import java.io.Serial;
import java.io.Serializable;

public record LoginRequestDTO (
    String username,
    String password
) implements Serializable {
  @Serial
  private static final long serialVersionUID = 1002L;
}
