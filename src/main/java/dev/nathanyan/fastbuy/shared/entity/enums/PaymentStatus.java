package dev.nathanyan.fastbuy.shared.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentStatus {
  PENDING("Payment is pending confirmation"),
  COMPLETED("Payment completed successfully"),
  FAILED("Payment failed"),
  REFUNDED("Payment refunded");

  private final String description;
}
