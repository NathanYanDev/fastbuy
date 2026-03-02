package dev.nathanyan.fastbuy.security;

public final class SecurityConstants {
  private SecurityConstants() {}

  public static final String[] PUBLIC_ENDPOINTS = {
      "/auth/**",
      "/v3/api-docs/**",
      "/swagger-ui/**",
      "/actuator/health"
  };

  public static final String[] ADMIN_ENDPOINTS = {
      "/products/**",
      "/categories/**"
  };
}
