package dev.nathanyan.fastbuy.customer.dto;

import dev.nathanyan.fastbuy.shared.dto.address.AddressResponse;
import dev.nathanyan.fastbuy.shared.entity.enums.UserRole;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public record CustomerResponse(
    String id,
    String name,
    String email,
    String phone,
    String document,
    LocalDate birthDate,
    UserRole role,
    List<AddressResponse> addresses)
    implements Serializable {
  @Serial private static final long serialVersionUID = 1001L;
}
