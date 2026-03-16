package dev.nathanyan.fastbuy.customer.dto;

import dev.nathanyan.fastbuy.shared.validation.annotation.PhoneNumber;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

public record UpdateCustomerRequest(
    @NotBlank(message = "Name is required")
        @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
        String name,
    @NotBlank(message = "Phone is required") @PhoneNumber String phone,
    @NotNull(message = "Birth date is required") LocalDate birthDate)
    implements Serializable {
  @Serial private static final long serialVersionUID = 1001L;
}
