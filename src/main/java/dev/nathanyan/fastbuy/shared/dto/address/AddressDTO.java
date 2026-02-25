package dev.nathanyan.fastbuy.shared.dto.address;

import java.io.Serial;
import java.io.Serializable;

public record AddressDTO (
    String street,
    int number,
    String complement,
    String city,
    String state,
    String zipCode,
    String country
) implements Serializable {
  @Serial
  private static final long serialVersionUID = 1002L;
}
