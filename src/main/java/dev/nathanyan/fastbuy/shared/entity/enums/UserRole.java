package dev.nathanyan.fastbuy.shared.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {
  ADMIN("admin"),
  CUSTOMER("customer");

  private final String role;
}
