package dev.nathanyan.fastbuy.shared.dto.address;

import dev.nathanyan.fastbuy.shared.validation.annotation.CEP;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

public record AddressDTO(
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
}
