package dev.nathanyan.fastbuy.shared.dto.address;

import dev.nathanyan.fastbuy.shared.entity.AddressEntity;
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

  public static AddressResponse from(AddressEntity address) {
    return new AddressResponse(
        address.getId(),
        address.getStreet(),
        address.getNumber(),
        address.getComplement(),
        address.getNeighborhood(),
        address.getCity(),
        address.getState(),
        address.getZipCode(),
        address.getCountry(),
        address.getIsDefault());
  }
}
