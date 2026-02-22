package dev.nathanyan.fastbuy.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {
  ADMIN("admin"),
  CUSTOMER("customer");

  private final String role;
}
