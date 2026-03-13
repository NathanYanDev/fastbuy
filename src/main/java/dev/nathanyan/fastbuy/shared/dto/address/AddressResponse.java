package dev.nathanyan.fastbuy.shared.dto.address;

import java.io.Serial;
import java.io.Serializable;

public record AddressResponse(
    String id,
    String street,
    Integer number,
    String complement,
    String neighborhood,
    String city,
    String state,
    String zipCode,
    String country,
    Boolean isDefault)
    implements Serializable {
  @Serial private static final long serialVersionUID = 1001L;
}
