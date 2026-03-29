package dev.nathanyan.fastbuy.customer.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.nathanyan.fastbuy.config.SecurityConfig;
import dev.nathanyan.fastbuy.customer.dto.CustomerResponse;
import dev.nathanyan.fastbuy.customer.dto.UpdateCustomerRequest;
import dev.nathanyan.fastbuy.customer.dto.UpdateEmailRequest;
import dev.nathanyan.fastbuy.customer.dto.UpdatePasswordRequest;
import dev.nathanyan.fastbuy.customer.service.CustomerService;
import dev.nathanyan.fastbuy.security.*;
import dev.nathanyan.fastbuy.shared.dto.address.AddressRequest;
import dev.nathanyan.fastbuy.shared.dto.address.AddressResponse;
import dev.nathanyan.fastbuy.shared.entity.enums.UserRole;
import dev.nathanyan.fastbuy.shared.exception.InvalidPasswordException;
import dev.nathanyan.fastbuy.shared.exception.ResourceNotFoundException;
import dev.nathanyan.fastbuy.shared.exception.UserAlreadyExistsException;
import java.time.LocalDate;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = CustomerController.class)
@Import({SecurityConfig.class, JwtAuthFilter.class})
class CustomerControllerTest {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private CustomerService customerService;
  @MockitoBean private JwtService jwtService;
  @MockitoBean private UserDetailsServiceImpl userDetailsService;
  @MockitoBean private PasswordEncoder passwordEncoder;
  @MockitoBean private OAuth2UserServiceImpl oAuth2UserService;
  @MockitoBean private OAuth2SuccessHandler oAuth2SuccessHandler;

  private CustomerResponse customerResponse;
  private AddressRequest addressRequest;

  @BeforeEach
  void setUp() {
    customerResponse =
        new CustomerResponse(
            "1",
            "John Doe",
            "test@email.com",
            "(11) 99943-2843",
            "465.354.489-80",
            LocalDate.of(1998, 1, 1),
            UserRole.CUSTOMER,
            new ArrayList<>());

    addressRequest =
        new AddressRequest(
            "Rua das Flores", 100, null, "Centro", "Jundiaí", "SP", "13200-000", "Brasil", true);
  }

  private MockHttpServletRequestBuilder withAuth(MockHttpServletRequestBuilder request) {
    return request.with(SecurityMockMvcRequestPostProcessors.user("test@email.com"));
  }

  // GET /api/v1/customers/me
  @Test
  @DisplayName("Should get customer and return 200")
  void shouldGetCustomerAndReturn200() throws Exception {
    when(customerService.getCustomer("test@email.com")).thenReturn(customerResponse);

    mockMvc
        .perform(withAuth(get("/api/v1/customers/me")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("test@email.com"))
        .andExpect(jsonPath("$.name").value("John Doe"));
  }

  @Test
  @DisplayName("Should return 401 when not authenticated on GET /me")
  void shouldReturn401WhenNotAuthenticatedOnGetMe() throws Exception {
    mockMvc.perform(get("/api/v1/customers/me")).andExpect(status().isUnauthorized());
  }

  // PUT /api/v1/customers/me
  @Test
  @DisplayName("Should update customer and return 200")
  void shouldUpdateCustomerAndReturn200() throws Exception {
    UpdateCustomerRequest updateCustomerRequest =
        new UpdateCustomerRequest("John Doe Doe", "(11) 99943-2843", LocalDate.of(2012, 12, 12));
    CustomerResponse updatedResponse =
        new CustomerResponse(
            "1",
            "John Doe Doe", // nome atualizado
            "test@email.com",
            "(11) 99943-2843",
            "465.354.489-80",
            LocalDate.of(2012, 12, 12),
            UserRole.CUSTOMER,
            new ArrayList<>());

    when(customerService.updateCustomer("test@email.com", updateCustomerRequest))
        .thenReturn(updatedResponse);

    mockMvc
        .perform(
            withAuth(put("/api/v1/customers/me"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateCustomerRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("test@email.com"))
        .andExpect(jsonPath("$.name").value("John Doe Doe"));
  }

  @Test
  @DisplayName("Should return 400 when request is invalid")
  void shouldReturn400WhenRequestIsInvalid() throws Exception {
    mockMvc.perform(withAuth(put("/api/v1/customers/me"))).andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 401 when not authenticated on PUT /me")
  void shouldReturn401WhenNotAuthenticatedOnPutMe() throws Exception {
    mockMvc.perform(put("/api/v1/customers/me")).andExpect(status().isUnauthorized());
  }

  // PATCH /api/v1/customers/me/email
  @Test
  @DisplayName("Should update email and return 200")
  void shouldUpdateEmailAndReturn200() throws Exception {
    UpdateEmailRequest request = new UpdateEmailRequest("test@email.com");

    when(customerService.updateEmail("john.doe@email.com", request.email()))
        .thenReturn(customerResponse);

    mockMvc
        .perform(
            withAuth(patch("/api/v1/customers/me/email"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Should return 400 when email is invalid")
  void shouldReturn400WhenEmailIsInvalid() throws Exception {
    UpdateEmailRequest invalidEmailRequest = new UpdateEmailRequest("invalid.email");

    when(customerService.updateEmail("john.doe@email.com", invalidEmailRequest.email()))
        .thenThrow(new IllegalArgumentException("Invalid email"));

    mockMvc
        .perform(
            withAuth(patch("/api/v1/customers/me/email"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidEmailRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 409 when email already in use")
  void shouldReturn409WhenEmailAlreadyInUse() throws Exception {
    when(customerService.updateEmail(anyString(), anyString()))
        .thenThrow(new UserAlreadyExistsException("Email already exists"));

    mockMvc
        .perform(
            withAuth(patch("/api/v1/customers/me/email"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new UpdateEmailRequest("test@email.com"))))
        .andExpect(status().isConflict());
  }

  @Test
  @DisplayName("Should return 401 when not authenticated")
  void shouldReturn401WhenNotAuthenticated() throws Exception {
    mockMvc.perform(patch("/api/v1/customers/me/email")).andExpect(status().isUnauthorized());
  }

  // PATCH /api/v1/customers/me/password
  @Test
  @DisplayName("Should update password and return 200")
  void shouldUpdatePasswordAndReturn200() throws Exception {
    UpdatePasswordRequest request =
        new UpdatePasswordRequest("safetypassword123", "newpassword456");

    when(customerService.updatePassword("test@email.com", request)).thenReturn(customerResponse);

    mockMvc
        .perform(
            withAuth(patch("/api/v1/customers/me/password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("test@email.com"));
  }

  @Test
  @DisplayName("Should return 400 when password request is invalid")
  void shouldReturn400WhenPasswordRequestIsInvalid() throws Exception {
    UpdatePasswordRequest invalidRequest = new UpdatePasswordRequest("", "123");

    mockMvc
        .perform(
            withAuth(patch("/api/v1/customers/me/password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 401 when current password is wrong")
  void shouldReturn401WhenPasswordIsWrong() throws Exception {
    UpdatePasswordRequest request = new UpdatePasswordRequest("wrongpass", "newpassword456");

    when(customerService.updatePassword(any(), any()))
        .thenThrow(new InvalidPasswordException("Invalid password"));

    mockMvc
        .perform(
            withAuth(patch("/api/v1/customers/me/password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());
  }

  // POST /api/v1/customers/me/addresses
  @Test
  @DisplayName("Should add address and return 201")
  void shouldAddAddressAndReturn201() throws Exception {
    AddressResponse addressResponse =
        new AddressResponse(
            "address-1",
            "Rua das Flores",
            100,
            null,
            "Centro",
            "Jundiaí",
            "SP",
            "13200-000",
            "Brasil",
            true);

    when(customerService.addAddress("test@email.com", addressRequest)).thenReturn(addressResponse);

    mockMvc
        .perform(
            withAuth(post("/api/v1/customers/me/addresses"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value("address-1"))
        .andExpect(jsonPath("$.street").value("Rua das Flores"));
  }

  @Test
  @DisplayName("Should return 400 when address request is invalid")
  void shouldReturn400WhenAddressRequestIsInvalid() throws Exception {
    AddressRequest invalidRequest = new AddressRequest("", 0, null, "", "", "", "", "", null);

    mockMvc
        .perform(
            withAuth(post("/api/v1/customers/me/addresses"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  // PUT /api/v1/customers/me/addresses/{addressId}
  @Test
  @DisplayName("Should update address and return 200")
  void shouldUpdateAddressAndReturn200() throws Exception {
    when(customerService.updateAddress("test@email.com", "address-1", addressRequest))
        .thenReturn(customerResponse);

    mockMvc
        .perform(
            withAuth(put("/api/v1/customers/me/addresses/address-1"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("test@email.com"));
  }

  @Test
  @DisplayName("Should return 400 when update address request is invalid")
  void shouldReturn400WhenUpdateAddressRequestIsInvalid() throws Exception {
    AddressRequest invalidRequest = new AddressRequest("", 0, null, "", "", "", "", "", null);

    mockMvc
        .perform(
            withAuth(put("/api/v1/customers/me/addresses/address-1"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 404 when address not found on update")
  void shouldReturn404WhenAddressNotFoundOnUpdate() throws Exception {
    when(customerService.updateAddress(any(), eq("invalid-id"), any()))
        .thenThrow(new ResourceNotFoundException("Address not found"));

    mockMvc
        .perform(
            withAuth(put("/api/v1/customers/me/addresses/invalid-id"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressRequest)))
        .andExpect(status().isNotFound());
  }

  // DELETE /api/v1/customers/me/addresses/{addressId}
  @Test
  @DisplayName("Should delete address and return 204")
  void shouldDeleteAddressAndReturn204() throws Exception {
    doNothing().when(customerService).deleteAddress("test@email.com", "address-1");

    mockMvc
        .perform(withAuth(delete("/api/v1/customers/me/addresses/address-1")))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("Should return 404 when address not found on delete")
  void shouldReturn404WhenAddressNotFoundOnDelete() throws Exception {
    doThrow(new ResourceNotFoundException("Address not found"))
        .when(customerService)
        .deleteAddress(any(), eq("invalid-id"));

    mockMvc
        .perform(withAuth(delete("/api/v1/customers/me/addresses/invalid-id")))
        .andExpect(status().isNotFound());
  }

  // PATCH /api/v1/customers/me/addresses/{addressId}
  @Test
  @DisplayName("Should set default address and return 200")
  void shouldSetDefaultAddressAndReturn200() throws Exception {
    when(customerService.setDefaultAddress("test@email.com", "address-1"))
        .thenReturn(customerResponse);

    mockMvc
        .perform(withAuth(patch("/api/v1/customers/me/addresses/address-1")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("test@email.com"));
  }

  @Test
  @DisplayName("Should return 404 when address not found on set default")
  void shouldReturn404WhenAddressNotFoundOnSetDefault() throws Exception {
    when(customerService.setDefaultAddress(any(), eq("invalid-id")))
        .thenThrow(new ResourceNotFoundException("Address not found"));

    mockMvc
        .perform(withAuth(patch("/api/v1/customers/me/addresses/invalid-id")))
        .andExpect(status().isNotFound());
  }
}
