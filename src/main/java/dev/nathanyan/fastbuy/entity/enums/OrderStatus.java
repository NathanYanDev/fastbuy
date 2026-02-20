package dev.nathanyan.fastbuy.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {
  PENDING("Order received, waiting for confirmation"),
  PROCESSING("Order is being packed"),
  SHIPPED("Order is on the way"),
  DELIVERED("Order delivered successfully"),
  CANCELLED("Order was cancelled");

  private String description;

  public boolean isFinalStatus() {
    return this == DELIVERED || this == CANCELLED;
  }
}
