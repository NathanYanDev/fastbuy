package dev.nathanyan.fastbuy.security;

import dev.nathanyan.fastbuy.shared.entity.CustomerEntity;
import dev.nathanyan.fastbuy.shared.entity.RefreshTokenEntity;
import dev.nathanyan.fastbuy.shared.repository.CustomerRepository;
import dev.nathanyan.fastbuy.shared.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {
  @Value("${security.jwt.secret}")
  private String secret;

  @Getter
  @Value("${security.jwt.expiration}")
  private long expiration;

  @Value("${security.jwt.refresh-expiration}")
  private long refreshTokenDurationMs;

  private final CustomerRepository customerRepository;
  private final RefreshTokenRepository refreshTokenRepository;

  private SecretKey getSecretKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secret);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts
            .parser()
            .verifyWith(getSecretKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
  }

  public String generateToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails);
  }

  public String generateToken(Map<String, Object> claims, UserDetails userDetails) {
    return buildToken(claims, userDetails, expiration);
  }

  private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
    return Jwts
        .builder()
        .claims(extraClaims)
        .subject(userDetails.getUsername())
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(getSecretKey())
        .compact();
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    return getExpirationDate(token).before(new Date());
  }

  private Date getExpirationDate(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public String generateRefreshToken(UserDetails userDetails) {
    return buildToken(new HashMap<>(), userDetails, refreshTokenDurationMs);
  }

  public void saveRefreshToken(UserDetails userDetails, String refreshToken) {
    CustomerEntity customer = customerRepository.findByEmail(userDetails.getUsername())
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    refreshTokenRepository.deleteByCustomer(customer);

    RefreshTokenEntity token = RefreshTokenEntity.builder()
        .token(refreshToken)
        .customer(customer)
        .expiresAt(Instant.now().plusMillis(refreshTokenDurationMs))
        .build();

    refreshTokenRepository.save(token);
  }

  public boolean validateRefreshToken(String token, UserDetails userDetails) {
    final String username = extractUsername(token);

    boolean existsInDatabase = refreshTokenRepository.existsByTokenAndCustomer_Email(token, username);

    return username.equals(userDetails.getUsername())
        && !isTokenExpired(token)
        && existsInDatabase;
  }
}
