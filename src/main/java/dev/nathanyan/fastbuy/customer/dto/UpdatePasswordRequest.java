package dev.nathanyan.fastbuy.customer.dto;

import java.io.Serial;
import java.io.Serializable;

public record UpdatePasswordRequest(String currentPassword, String newPassword)
    implements Serializable {
  @Serial private static final long serialVersionUID = 1001L;
}
