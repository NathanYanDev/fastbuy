package dev.nathanyan.fastbuy.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.nathanyan.fastbuy.auth.controller.AuthController;
import dev.nathanyan.fastbuy.auth.dto.AuthResponse;
import dev.nathanyan.fastbuy.auth.dto.LoginRequest;
import dev.nathanyan.fastbuy.auth.dto.RegisterRequest;
import dev.nathanyan.fastbuy.auth.service.AuthService;
import dev.nathanyan.fastbuy.security.JwtService;
import dev.nathanyan.fastbuy.security.UserDetailsServiceImpl;
import dev.nathanyan.fastbuy.shared.dto.address.AddressDTO;
import dev.nathanyan.fastbuy.shared.exception.InvalidTokenException;
import dev.nathanyan.fastbuy.shared.exception.UserAlreadyExistsException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {
  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private AuthService authService;

  @MockitoBean private JwtService jwtService;

  @MockitoBean private UserDetailsServiceImpl userDetailsService;

  private RegisterRequest registerRequest;
  private LoginRequest loginRequest;

  @BeforeEach
  void setUp() {
    registerRequest =
        new RegisterRequest(
            "john.doe@email.com",
            "password123",
            "Test Customer",
            "042.815.546-45",
            "(11) 99587-4754",
            LocalDate.of(2001, 5, 1),
            List.of(
                new AddressDTO(
                    "Rua A",
                    123,
                    null,
                    "Bairro B",
                    "São Paulo",
                    "SP",
                    "13200-000",
                    "Brasil",
                    true)));

    loginRequest = new LoginRequest("john.doe@email.com", "password123");
  }

  @Test
  @DisplayName("Should register customer and return 201")
  void shouldRegisterCustomerAndReturn201() throws Exception {
    AuthResponse response = new AuthResponse("jwt-token", "refresh-token", 900000L);

    when(authService.register(any(RegisterRequest.class))).thenReturn(response);

    mockMvc
        .perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.token").value("jwt-token"))
        .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
        .andExpect(jsonPath("$.expiresIn").value(900000L));
  }

  @Test
  @DisplayName("Should return 409 when email already exists")
  void shouldReturn409WhenEmailAlreadyExists() throws Exception {
    when(authService.register(any(RegisterRequest.class)))
        .thenThrow(new UserAlreadyExistsException("User already exists"));

    mockMvc
        .perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isConflict());
  }

  @Test
  @DisplayName("Should return 400 when request is invalid")
  void shouldReturn400WhenRequestIsInvalid() throws Exception {
    RegisterRequest request =
        new RegisterRequest(
            "invalid-email",
            "123",
            "",
            "000.000.000-00",
            "(00) 00000-0000",
            LocalDate.of(2001, 5, 1),
            List.of());

    mockMvc
        .perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should login customer and return 200")
  void shouldLoginCustomerAndReturn200() throws Exception {
    AuthResponse response = new AuthResponse("jwt-token", "refresh-token", 900000L);

    when(authService.login(any(LoginRequest.class))).thenReturn(response);

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value("jwt-token"))
        .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
        .andExpect(jsonPath("$.expiresIn").value(900000L));
  }

  @Test
  @DisplayName("Should return 401 when credentials are invalid")
  void shouldReturn401WhenCredentialsAreInvalid() throws Exception {
    when(authService.login(any(LoginRequest.class)))
        .thenThrow(new BadCredentialsException("Bad credentials"));

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Should return 400 when login request is invalid")
  void shouldReturn400WhenLoginRequestIsInvalid() throws Exception {
    LoginRequest invalidRequest = new LoginRequest("invalid-email", "123");

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should refresh token and return 200")
  void shouldRefreshTokenAndReturn200() throws Exception {
    AuthResponse response = new AuthResponse("new-jwt-token", "new-refresh-token", 900000L);

    when(authService.refreshToken(any())).thenReturn(response);

    mockMvc
        .perform(
            post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer valid-refresh-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value("new-jwt-token"))
        .andExpect(jsonPath("$.refreshToken").value("new-refresh-token"))
        .andExpect(jsonPath("$.expiresIn").value(900000L));
  }

  @Test
  @DisplayName("Should return 401 when refresh token is invalid")
  void shouldReturn401WhenRefreshTokenIsInvalid() throws Exception {
    when(authService.refreshToken(any()))
        .thenThrow(new InvalidTokenException("Refresh token is invalid or expired"));

    mockMvc
        .perform(
            post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer invalid-refresh-token"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Should logout and return 204")
  void shouldLogoutAndReturn204() throws Exception {
    doNothing().when(authService).logout(any());

    mockMvc
        .perform(
            post("/api/v1/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer valid-jwt-token"))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("Should return 401 when logout without token")
  void shouldReturn401WhenLogoutWithoutToken() throws Exception {
    mockMvc
        .perform(post("/api/v1/auth/logout").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }
}
