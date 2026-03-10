package dev.nathanyan.fastbuy.auth;

import dev.nathanyan.fastbuy.auth.dto.AuthResponse;
import dev.nathanyan.fastbuy.auth.dto.LoginRequest;
import dev.nathanyan.fastbuy.auth.dto.RegisterRequest;
import dev.nathanyan.fastbuy.auth.service.AuthService;
import dev.nathanyan.fastbuy.security.JwtService;
import dev.nathanyan.fastbuy.shared.dto.address.AddressDTO;
import dev.nathanyan.fastbuy.shared.entity.CartEntity;
import dev.nathanyan.fastbuy.shared.entity.CustomerEntity;
import dev.nathanyan.fastbuy.shared.entity.enums.UserRole;
import dev.nathanyan.fastbuy.shared.exception.InvalidTokenException;
import dev.nathanyan.fastbuy.shared.exception.UserAlreadyExistsException;
import dev.nathanyan.fastbuy.shared.repository.CartRepository;
import dev.nathanyan.fastbuy.shared.repository.CustomerRepository;
import dev.nathanyan.fastbuy.shared.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
  @InjectMocks
  private AuthService authService;

  @Mock
  private CustomerRepository customerRepository;

  @Mock
  private CartRepository cartRepository;

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private JwtService jwtService;

  @Mock
  private RefreshTokenRepository refreshTokenRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  private RegisterRequest registerRequest;
  private CustomerEntity customer;
  private LoginRequest loginRequest;

  @BeforeEach
  void setUp() {
    registerRequest = new RegisterRequest(
        "john.doe@email.com",
        "password123",
        "Test Customer",
        List.of(new AddressDTO(
            "Rua A",
            123,
            null,
            "São Paulo",
            "SP",
            "13200-000",
            "Brasil",
            true)
        )
    );

    loginRequest = new LoginRequest("john.doe@email.com", "password123");

    customer = CustomerEntity.builder()
        .email("john.doe@email.com")
        .name("Test Customer")
        .role(UserRole.CUSTOMER)
        .build();

  }

  // Register tests
  @Test
  @DisplayName("Should register customer successfully")
  void shouldRegisterCustomerSuccessfully() {
    when(customerRepository.findByEmail(registerRequest.username()))
        .thenReturn(Optional.empty());
    when(passwordEncoder.encode(registerRequest.password()))
        .thenReturn("encodedPassword");
    when(customerRepository.save(any(CustomerEntity.class)))
        .thenReturn(customer);
    when(jwtService.generateToken(any()))
        .thenReturn("jwt-token");
    when(jwtService.generateRefreshToken(any()))
        .thenReturn("refresh-token");
    when(jwtService.getExpiration())
        .thenReturn(90000L);

    AuthResponse authResponse = authService.register(registerRequest);

    assertNotNull(authResponse);
    assertEquals("jwt-token", authResponse.token());
    assertEquals("refresh-token", authResponse.refreshToken());
    verify(cartRepository, times(1)).save(any(CartEntity.class));
    verify(jwtService, times(1)).saveRefreshToken(any(), eq("refresh-token"));
  }

  @Test
  @DisplayName("Should throw exception when customer already exists")
  void shouldThrowExceptionWhenCustomerExists() {
    when(customerRepository.findByEmail(registerRequest.username())).thenReturn(Optional.of(customer));

    assertThrows(UserAlreadyExistsException.class, () -> authService.register(registerRequest));

    verify(customerRepository, never()).save(any());
    verify(cartRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should create cart when registering customer")
  void shouldCreateCartWhenRegisteringCustomer() {
    when(customerRepository.findByEmail(any())).thenReturn(Optional.empty());
    when(customerRepository.save(any())).thenReturn(customer);
    when(jwtService.generateToken(any())).thenReturn("jwt-token");
    when(jwtService.generateRefreshToken(any())).thenReturn("refresh-token");

    authService.register(registerRequest);

    ArgumentCaptor<CartEntity> captor = ArgumentCaptor.forClass(CartEntity.class);
    verify(cartRepository, times(1)).save(captor.capture());
    assertEquals(customer.getId(), captor.getValue().getCustomer().getId());
  }

  @Test
  @DisplayName("Should save refresh token when registering")
  void shouldSaveRefreshTokenWhenRegistering() {
    when(customerRepository.findByEmail(any())).thenReturn(Optional.empty());
    when(customerRepository.save(any())).thenReturn(customer);
    when(jwtService.generateToken(any())).thenReturn("jwt-token");
    when(jwtService.generateRefreshToken(any())).thenReturn("refresh-token");
    when(jwtService.getExpiration()).thenReturn(90000L);

    AuthResponse authResponse = authService.register(registerRequest);

    verify(jwtService, times(1)).saveRefreshToken(any(), eq("refresh-token"));
  }

  @Test
  @DisplayName("Should encode password when registering")
  void shouldEncodePasswordWhenRegistering() {
    when(customerRepository.findByEmail(registerRequest.username()))
        .thenReturn(Optional.empty());
    when(passwordEncoder.encode(registerRequest.password()))
        .thenReturn("encodedPassword");
    when(customerRepository.save(any(CustomerEntity.class)))
        .thenReturn(customer);

    authService.register(registerRequest);

    verify(passwordEncoder, times(1)).encode(registerRequest.password());
    ArgumentCaptor<CustomerEntity> captor = ArgumentCaptor.forClass(CustomerEntity.class);
    verify(customerRepository, times(1)).save(captor.capture());
    assertEquals("encodedPassword", captor.getValue().getPassword());
  }

  // Login tests
  @Test
  @DisplayName("Should login customer successfully")
  void shouldLoginCustomerSuccessfully() {
    when(authenticationManager.authenticate(any())).thenReturn(new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));
    when(customerRepository.findByEmail(loginRequest.username())).thenReturn(Optional.of(customer));
    when(jwtService.generateToken(any()))
        .thenReturn("jwt-token");
    when(jwtService.generateRefreshToken(any()))
        .thenReturn("refresh-token");
    when(jwtService.getExpiration())
        .thenReturn(90000L);

    AuthResponse authResponse = authService.login(loginRequest);

    assertNotNull(authResponse);
    assertEquals("jwt-token", authResponse.token());
    assertEquals("refresh-token", authResponse.refreshToken());
    verify(authenticationManager, times(1)).authenticate(any());
    verify(jwtService, times(1)).saveRefreshToken(any(), eq("refresh-token"));
  }

  @Test
  @DisplayName("Should throw exception when user not found")
  void shouldThrowExceptionWhenUserNotFound() {
    when(authenticationManager.authenticate(any())).thenReturn(new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));
    when(customerRepository.findByEmail(loginRequest.username())).thenReturn(Optional.empty());

    assertThrows(UsernameNotFoundException.class, () -> authService.login(loginRequest));

    verify(jwtService, never()).generateToken(any());
  }

  @Test
  @DisplayName("Should throw exception when password is wrong")
  void shouldThrowExceptionWhenPasswordIsWrong() {
    when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

    assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));

    verify(customerRepository, never()).findByEmail(any());
    verify(jwtService, never()).generateToken(any());
  }

  @Test
  @DisplayName("Should logout user successfully")
  void shouldLogoutUserSuccessfully() {
    String token = "valid-jwt-token";

    when(jwtService.extractUsername(token)).thenReturn(loginRequest.username());
    when(customerRepository.findByEmail(loginRequest.username())).thenReturn(Optional.of(customer));

    authService.logout(token);

    verify(jwtService, times(1)).extractUsername(token);
    verify(customerRepository, times(1)).findByEmail(loginRequest.username());
    verify(refreshTokenRepository, times(1)).deleteByCustomer(customer);
  }

  @Test
  @DisplayName("Should refresh token successfully")
  void shouldRefreshTokenSuccessfully() {
    String token = "valid-jwt-token";

    when(jwtService.extractUsername(token)).thenReturn(loginRequest.username());
    when(customerRepository.findByEmail(loginRequest.username())).thenReturn(Optional.of(customer));
    when(jwtService.validateRefreshToken(token, customer)).thenReturn(true);
    when(jwtService.generateToken(any())).thenReturn("new-access-token");
    when(jwtService.generateRefreshToken(any())).thenReturn("new-refresh-token");
    when(jwtService.getExpiration()).thenReturn(90000L);

    AuthResponse authResponse = authService.refreshToken(token);

    assertNotNull(authResponse);
    assertEquals("new-access-token", authResponse.token());
    assertEquals("new-refresh-token", authResponse.refreshToken());
    verify(jwtService, times(1)).saveRefreshToken(any(), eq("new-refresh-token"));
  }

  @Test
  @DisplayName("Should throw exception when refresh token is invalid")
  void shouldThrowExceptionWhenRefreshTokenIsInvalid() {
    String token = "valid-jwt-token";

    when(jwtService.extractUsername(token)).thenReturn(loginRequest.username());
    when(customerRepository.findByEmail(loginRequest.username())).thenReturn(Optional.of(customer));
    when(jwtService.validateRefreshToken(token, customer)).thenReturn(false);

    assertThrows(InvalidTokenException.class, () -> authService.refreshToken(token));

    verify(jwtService, never()).generateToken(any());
    verify(jwtService, never()).saveRefreshToken(any(), any());
  }
}
