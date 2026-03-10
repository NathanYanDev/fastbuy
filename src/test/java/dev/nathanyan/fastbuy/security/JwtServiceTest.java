package dev.nathanyan.fastbuy.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.nathanyan.fastbuy.shared.entity.CustomerEntity;
import dev.nathanyan.fastbuy.shared.entity.RefreshTokenEntity;
import dev.nathanyan.fastbuy.shared.entity.enums.UserRole;
import dev.nathanyan.fastbuy.shared.repository.CustomerRepository;
import dev.nathanyan.fastbuy.shared.repository.RefreshTokenRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

  @InjectMocks private JwtService jwtService;

  @Mock private CustomerRepository customerRepository;

  @Mock private RefreshTokenRepository refreshTokenRepository;

  private CustomerEntity customer;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(
        jwtService, "secret", "dGhpcy1pcy1hLXZlcnktc2VjdXJlLXNlY3JldC1rZXktZm9yLWRldg==");
    ReflectionTestUtils.setField(jwtService, "expiration", 900000L);
    ReflectionTestUtils.setField(jwtService, "refreshTokenDurationMs", 2592000000L);

    customer = CustomerEntity.builder().email("john.doe@email.com").role(UserRole.CUSTOMER).build();
  }

  @Test
  @DisplayName("Should generate valid token")
  void shouldGenerateValidToken() {
    String token = jwtService.generateToken(customer);

    assertNotNull(token);
    assertEquals("john.doe@email.com", jwtService.extractUsername(token));
  }

  @Test
  @DisplayName("Should validate token successfully")
  void shouldValidateTokenSuccessfully() {
    String token = jwtService.generateToken(customer);

    assertTrue(jwtService.isTokenValid(token, customer));
  }

  @Test
  @DisplayName("Should save refresh token and delete previous one")
  void shouldSaveRefreshTokenAndDeletePreviousOne() {
    when(customerRepository.findByEmail(customer.getUsername())).thenReturn(Optional.of(customer));

    String refreshToken = jwtService.generateRefreshToken(customer);
    jwtService.saveRefreshToken(customer, refreshToken);

    verify(refreshTokenRepository, times(1)).deleteByCustomer(customer);
    verify(refreshTokenRepository, times(1)).save(any(RefreshTokenEntity.class));
  }
}
