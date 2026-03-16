package dev.nathanyan.fastbuy.customer.dto;

import dev.nathanyan.fastbuy.shared.dto.address.AddressResponse;
import dev.nathanyan.fastbuy.shared.entity.CustomerEntity;
import dev.nathanyan.fastbuy.shared.entity.enums.UserRole;
import dev.nathanyan.fastbuy.shared.util.DocumentUtils;
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

  public static CustomerResponse from(CustomerEntity customer) {
    return new CustomerResponse(
        customer.getId(),
        customer.getName(),
        customer.getUsername(),
        customer.getPhone(),
        DocumentUtils.maskDocument(customer.getDocument()),
        customer.getBirthDate(),
        customer.getRole(),
        customer.getAddresses().stream()
            .map(AddressResponse::from)
            .toList()
    );
  }
}
