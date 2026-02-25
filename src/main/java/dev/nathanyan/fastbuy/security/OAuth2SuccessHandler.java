package dev.nathanyan.fastbuy.security;

import dev.nathanyan.fastbuy.shared.entity.CustomerEntity;
import dev.nathanyan.fastbuy.shared.entity.RefreshTokenEntity;
import dev.nathanyan.fastbuy.shared.repository.CustomerRepository;
import dev.nathanyan.fastbuy.shared.repository.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
  private final JwtService jwtService;
  private final CustomerRepository customerRepository;
  private final RefreshTokenRepository refreshTokenRepository;

  @Value("${security.jwt.refresh-expiration}")
  private long refreshTokenDurationMs;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
    String email = oAuth2User.getAttribute("email");

    CustomerEntity customer = customerRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

    String accessToken = jwtService.generateToken(customer);
    String refreshToken = jwtService.generateRefreshToken(customer);

    saveRefreshToken(customer, refreshToken);

    String redirectUrl = UriComponentsBuilder.fromUriString("/auth/oauth2/callback").queryParam("accessToken", accessToken).queryParam("refreshToken", refreshToken).build().toUriString();

    response.sendRedirect(redirectUrl);
  }

  private void saveRefreshToken(CustomerEntity customer, String token) {
    refreshTokenRepository.deleteByCustomer(customer);

    RefreshTokenEntity refreshToken = RefreshTokenEntity.builder().token(token).customer(customer).expiresAt(Instant.now().plusMillis(refreshTokenDurationMs)).build();

    refreshTokenRepository.save(refreshToken);
  }
}
