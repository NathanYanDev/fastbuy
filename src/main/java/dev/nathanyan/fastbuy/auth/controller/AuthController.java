package dev.nathanyan.fastbuy.auth.controller;

import dev.nathanyan.fastbuy.auth.dto.AuthResponse;
import dev.nathanyan.fastbuy.auth.dto.LoginRequest;
import dev.nathanyan.fastbuy.auth.dto.RegisterRequest;
import dev.nathanyan.fastbuy.auth.service.AuthService;
import dev.nathanyan.fastbuy.security.ApiConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping(ApiConstants.AUTH_PREFIX)
public class AuthController {
  private final AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest body) {
    AuthResponse response = authService.register(body);

    return ResponseEntity.created(URI.create(ApiConstants.CUSTOMER_PREFIX + "/me")).body(response);
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest body) {
    AuthResponse response = authService.login(body);

    return ResponseEntity.ok(response);
  }

  @PostMapping("/refresh")
  public ResponseEntity<AuthResponse> refreshToken(@RequestHeader("Authorization") String authHeader) {
    String token = authHeader.substring(7);
    AuthResponse response = authService.refreshToken(token);

    return ResponseEntity.ok(response);
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
    String token = authHeader.substring(7);
    authService.logout(token);

    return ResponseEntity.noContent().build();
  }
}
