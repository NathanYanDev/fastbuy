package dev.nathanyan.fastbuy.security;

public final class SecurityConstants {
  public static final String[] PUBLIC_ENDPOINTS = {
    "/api/v1/auth/**", "/v3/api-docs/**", "/swagger-ui/**", "/actuator/health"
  };
  public static final String[] ADMIN_ENDPOINTS = {"/products/**", "/categories/**"};

  private SecurityConstants() {}
}
