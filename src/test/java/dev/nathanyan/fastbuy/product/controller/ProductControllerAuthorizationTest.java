package dev.nathanyan.fastbuy.product.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.nathanyan.fastbuy.BaseControllerTest;
import dev.nathanyan.fastbuy.product.dto.ProductRequest;
import dev.nathanyan.fastbuy.product.service.ProductService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = ProductController.class)
class ProductControllerAuthorizationTest extends BaseControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockitoBean private ProductService productService;

  private ProductRequest productRequest;

  @BeforeEach
  void setUp() {
    productRequest =
        new ProductRequest(
            "Notebook Dell Inspiron", "Notebook para uso profissional", List.of("cat-1"));
  }

  @Test
  @DisplayName("Should return 403 when creating product without admin role")
  void shouldReturn403WhenCreatingProductWithoutAdminRole() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/products")
                .with(user("customer@email.com").roles("CUSTOMER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequest)))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("Should return 403 when updating product without admin role")
  void shouldReturn403WhenUpdatingProductWithoutAdminRole() throws Exception {
    mockMvc
        .perform(
            put("/api/v1/products/prod-1")
                .with(user("customer@email.com").roles("CUSTOMER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequest)))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("Should return 403 when deleting product without admin role")
  void shouldReturn403WhenDeletingProductWithoutAdminRole() throws Exception {
    mockMvc
        .perform(
            delete("/api/v1/products/prod-1").with(user("customer@email.com").roles("CUSTOMER")))
        .andExpect(status().isForbidden());
  }
}
