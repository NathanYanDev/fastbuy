package dev.nathanyan.fastbuy.auth.service;

import dev.nathanyan.fastbuy.auth.dto.AuthResponse;
import dev.nathanyan.fastbuy.auth.dto.RegisterRequest;
import dev.nathanyan.fastbuy.security.JwtService;
import dev.nathanyan.fastbuy.shared.entity.AddressEntity;
import dev.nathanyan.fastbuy.shared.entity.CartEntity;
import dev.nathanyan.fastbuy.shared.entity.CustomerEntity;
import dev.nathanyan.fastbuy.shared.entity.enums.UserRole;
import dev.nathanyan.fastbuy.shared.exception.UserAlreadyExistsException;
import dev.nathanyan.fastbuy.shared.repository.CartRepository;
import dev.nathanyan.fastbuy.shared.repository.CustomerRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthService implements UserDetailsService {

  private final CustomerRepository customerRepository;
  private final PasswordEncoder passwordEncoder;
  private final CartRepository cartRepository;
  private final JwtService jwtService;

  public AuthService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder, CartRepository cartRepository, JwtService jwtService) {
    this.customerRepository = customerRepository;
    this.passwordEncoder = passwordEncoder;
    this.cartRepository = cartRepository;
    this.jwtService = jwtService;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return customerRepository
        .findByEmail(username)
        .map(customer -> new org.springframework.security.core.userdetails
            .User(
              customer.getEmail(),
              customer.getPassword(),
              new ArrayList<>()
            )
        )
      .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }

  public AuthResponse register(RegisterRequest request) {
    if(customerRepository.findByEmail(request.username()).isPresent()) throw new UserAlreadyExistsException("User already exists" + request.username());

    CustomerEntity customer = CustomerEntity
        .builder()
        .email(request.username())
        .password(passwordEncoder.encode(request.password()))
        .name(request.name())
        .role(UserRole.CUSTOMER)
        .build();

    List<AddressEntity> addresses = request
        .addresses()
        .stream()
        .map(address -> AddressEntity
            .builder()
            .customer(customer)
            .street(address.street())
            .number(address.number())
            .complement(address.complement())
            .city(address.city())
            .state(address.state())
            .zipCode(address.zipCode())
            .country(address.country())
            .isDefault(address.isDefault())
            .build())
        .toList();

    customer.setAddresses(addresses);
    customerRepository.save(customer);

    CartEntity cart = CartEntity.builder().customer(customer).build();

    cartRepository.save(cart);

    String jwtToken = jwtService.generateToken(customer);
    String refreshToken = jwtService.generateRefreshToken(customer);
    jwtService.saveRefreshToken(customer, refreshToken);

    return new AuthResponse(jwtToken, jwtService.getExpiration());
  }
}
