package dev.nathanyan.fastbuy.shared.dto.address;

import dev.nathanyan.fastbuy.shared.entity.AddressEntity;
import dev.nathanyan.fastbuy.shared.entity.CustomerEntity;
import dev.nathanyan.fastbuy.shared.validation.annotation.CEP;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

public record AddressRequest(
    @NotBlank(message = "Street is required") String street,
    @Min(value = 1, message = "Number is required") int number,
    String complement,
    @NotBlank(message = "Neighborhood is required") String neighborhood,
    @NotBlank(message = "City is required") String city,
    @NotBlank(message = "State is required") String state,
    @NotBlank(message = "Zip code is required") @CEP String zipCode,
    @NotBlank(message = "Country is required") String country,
    @NotNull(message = "Default address flag is required") Boolean isDefault)
    implements Serializable {
  @Serial private static final long serialVersionUID = 1002L;

  public static AddressEntity toEntity(AddressRequest address, CustomerEntity customer) {
    return AddressEntity.builder()
        .customer(customer)
        .street(address.street())
        .number(address.number())
        .complement(address.complement())
        .neighborhood(address.neighborhood())
        .city(address.city())
        .state(address.state())
        .zipCode(address.zipCode())
        .country(address.country())
        .isDefault(address.isDefault())
        .build();
  }

  public static void updateEntity(AddressEntity address, AddressRequest request) {
    address.setStreet(request.street());
    address.setNumber(request.number());
    address.setComplement(request.complement());
    address.setNeighborhood(request.neighborhood());
    address.setCity(request.city());
    address.setState(request.state());
    address.setZipCode(request.zipCode());
    address.setCountry(request.country());
    address.setIsDefault(request.isDefault());
  }
}
