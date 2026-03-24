package dev.nathanyan.fastbuy.customer.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.nathanyan.fastbuy.customer.dto.CustomerResponse;
import dev.nathanyan.fastbuy.customer.service.CustomerService;
import dev.nathanyan.fastbuy.security.JwtService;
import dev.nathanyan.fastbuy.security.UserDetailsServiceImpl;
import dev.nathanyan.fastbuy.shared.dto.address.AddressRequest;
import dev.nathanyan.fastbuy.shared.entity.enums.UserRole;
import dev.nathanyan.fastbuy.shared.exception.InvalidTokenException;
import java.time.LocalDate;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = CustomerController.class)
class CustomerControllerTest {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private CustomerService customerService;
  @MockitoBean private JwtService jwtService;
  @MockitoBean private UserDetailsServiceImpl userDetailsService;

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
  @DisplayName("Should return 401 when not authenticated")
  void shouldReturn401WhenNotAuthenticated() throws Exception {
    when(customerService.getCustomer("test@email.com"))
        .thenThrow(new InvalidTokenException("Invalid token"));

    mockMvc.perform(get("/api/v1/customers/me")).andExpect(status().isUnauthorized());
  }
}
