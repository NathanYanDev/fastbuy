package dev.nathanyan.fastbuy.customer.service;

import dev.nathanyan.fastbuy.customer.dto.CustomerResponse;
import dev.nathanyan.fastbuy.security.JwtService;
import dev.nathanyan.fastbuy.shared.dto.address.AddressResponse;
import dev.nathanyan.fastbuy.shared.entity.CustomerEntity;
import dev.nathanyan.fastbuy.shared.exception.InvalidTokenException;
import dev.nathanyan.fastbuy.shared.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {
  private final CustomerRepository customerRepository;
  private final JwtService jwtService;

  public CustomerResponse getCustomer(String token) {
    String username = jwtService.extractUsername(token);

    if (username == null) {
      throw new InvalidTokenException("Invalid token");
    }

    CustomerEntity customer =
        customerRepository
            .findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    return new CustomerResponse(
        customer.getId(),
        customer.getName(),
        customer.getEmail(),
        customer.getPhone(),
        customer.getDocument(),
        customer.getBirthDate(),
        customer.getRole(),
        customer.getAddresses().stream()
            .map(
                address ->
                    new AddressResponse(
                        address.getId(),
                        address.getStreet(),
                        address.getNumber(),
                        address.getComplement(),
                        address.getNeighborhood(),
                        address.getCity(),
                        address.getState(),
                        address.getZipCode(),
                        address.getCountry(),
                        address.getIsDefault()))
            .toList());
  }
}
