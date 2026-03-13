package dev.nathanyan.fastbuy.auth.service;

import dev.nathanyan.fastbuy.auth.dto.AuthResponse;
import dev.nathanyan.fastbuy.auth.dto.LoginRequest;
import dev.nathanyan.fastbuy.auth.dto.RegisterRequest;
import dev.nathanyan.fastbuy.security.JwtService;
import dev.nathanyan.fastbuy.shared.entity.AddressEntity;
import dev.nathanyan.fastbuy.shared.entity.CartEntity;
import dev.nathanyan.fastbuy.shared.entity.CustomerEntity;
import dev.nathanyan.fastbuy.shared.entity.enums.UserRole;
import dev.nathanyan.fastbuy.shared.exception.InvalidTokenException;
import dev.nathanyan.fastbuy.shared.exception.UserAlreadyExistsException;
import dev.nathanyan.fastbuy.shared.repository.CartRepository;
import dev.nathanyan.fastbuy.shared.repository.CustomerRepository;
import dev.nathanyan.fastbuy.shared.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final CustomerRepository customerRepository;
  private final PasswordEncoder passwordEncoder;
  private final CartRepository cartRepository;
  private final JwtService jwtService;
  private final RefreshTokenRepository refreshTokenRepository;

  @Lazy private final AuthenticationManager authenticationManager;

  @Transactional
  public AuthResponse register(RegisterRequest request) {
    if (customerRepository.findByEmail(request.username()).isPresent())
      throw new UserAlreadyExistsException("User already exists" + request.username());

    CustomerEntity customer =
        CustomerEntity.builder()
            .email(request.username())
            .password(passwordEncoder.encode(request.password()))
            .name(request.name())
            .document(request.document())
            .phone(request.phone())
            .birthDate(request.birthDate())
            .role(UserRole.CUSTOMER)
            .build();

    List<AddressEntity> addresses =
        request.addresses().stream()
            .map(
                address ->
                    AddressEntity.builder()
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
                        .build())
            .toList();

    customer.setAddresses(addresses);
    customerRepository.save(customer);

    CartEntity cart = CartEntity.builder().customer(customer).build();

    cartRepository.save(cart);

    String jwtToken = jwtService.generateToken(customer);
    String refreshToken = jwtService.generateRefreshToken(customer);
    jwtService.saveRefreshToken(customer, refreshToken);

    return new AuthResponse(jwtToken, refreshToken, jwtService.getExpiration());
  }

  @Transactional
  public AuthResponse login(LoginRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.username(), request.password()));

    CustomerEntity customer =
        customerRepository
            .findByEmail(request.username())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    String jwtToken = jwtService.generateToken(customer);
    String refreshToken = jwtService.generateRefreshToken(customer);
    jwtService.saveRefreshToken(customer, refreshToken);

    return new AuthResponse(jwtToken, refreshToken, jwtService.getExpiration());
  }

  @Transactional
  public AuthResponse refreshToken(String token) {
    String username = jwtService.extractUsername(token);
    CustomerEntity customer =
        customerRepository
            .findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

    if (!jwtService.validateRefreshToken(token, customer)) {
      throw new InvalidTokenException("Refresh token is invalid or expired");
    }

    String newAccessToken = jwtService.generateToken(customer);
    String newRefreshToken = jwtService.generateRefreshToken(customer);

    jwtService.saveRefreshToken(customer, newRefreshToken);

    return new AuthResponse(newAccessToken, newRefreshToken, jwtService.getExpiration());
  }

  @Transactional
  public void logout(String token) {
    String username = jwtService.extractUsername(token);

    CustomerEntity customer =
        customerRepository
            .findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

    refreshTokenRepository.deleteByCustomer(customer);
  }
}
