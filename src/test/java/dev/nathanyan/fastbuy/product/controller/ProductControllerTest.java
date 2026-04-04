package dev.nathanyan.fastbuy.product.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.nathanyan.fastbuy.BaseControllerTest;
import dev.nathanyan.fastbuy.product.dto.*;
import dev.nathanyan.fastbuy.product.service.ProductService;
import dev.nathanyan.fastbuy.shared.exception.ResourceNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest extends BaseControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private ProductService productService;

  private ProductSummaryResponse productSummaryResponse;
  private ProductDetailResponse productDetailResponse;
  private ProductBaseResponse productBaseResponse;
  private ProductRequest productRequest;

  @BeforeEach
  void setUp() {
    productSummaryResponse =
        new ProductSummaryResponse(
            "prod-1",
            "Notebook Dell Inspiron",
            List.of(new CategoryNameResponse("cat-1", "Notebooks", "")),
            new VariantSummaryResponse(
                "var-1",
                "Dell i7 16GB",
                new BigDecimal("4599.90"),
                "https://cloudinary.com/cover.jpg",
                10,
                true),
            List.of());

    productDetailResponse =
        new ProductDetailResponse(
            "prod-1",
            "Notebook Dell Inspiron",
            List.of(new CategoryNameResponse("cat-1", "Notebooks", "")),
            List.of());

    productBaseResponse =
        new ProductBaseResponse(
            "prod-1",
            "Notebook Dell Inspiron",
            "Notebook para uso profissional",
            false,
            List.of(new CategoryNameResponse("cat-1", "Notebooks", "")));

    productRequest =
        new ProductRequest(
            "Notebook Dell Inspiron", "Notebook para uso profissional", List.of("cat-1"));
  }

  @Test
  @DisplayName("Should list products and return 200")
  void shouldListProductsAndReturn200() throws Exception {
    Page<ProductSummaryResponse> page = new PageImpl<>(List.of(productSummaryResponse));

    when(productService.getAllProducts(0, 20, "createdAt", "desc")).thenReturn(page);

    mockMvc
        .perform(get("/api/v1/products"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value("prod-1"))
        .andExpect(jsonPath("$.content[0].name").value("Notebook Dell Inspiron"))
        .andExpect(jsonPath("$.totalElements").value(1));
  }

  @Test
  @DisplayName("Should list products with custom params and return 200")
  void shouldListProductsWithCustomParamsAndReturn200() throws Exception {
    Page<ProductSummaryResponse> page = new PageImpl<>(List.of(productSummaryResponse));

    when(productService.getAllProducts(1, 10, "name", "asc")).thenReturn(page);

    mockMvc
        .perform(
            get("/api/v1/products")
                .param("page", "1")
                .param("size", "10")
                .param("sortBy", "name")
                .param("direction", "asc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value("prod-1"));
  }

  @Test
  @DisplayName("Should return empty page when no products found")
  void shouldReturnEmptyPageWhenNoProductsFound() throws Exception {
    Page<ProductSummaryResponse> emptyPage = new PageImpl<>(List.of());

    when(productService.getAllProducts(0, 20, "createdAt", "desc")).thenReturn(emptyPage);

    mockMvc
        .perform(get("/api/v1/products"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isEmpty())
        .andExpect(jsonPath("$.totalElements").value(0));
  }

  @Test
  @DisplayName("Should get product by id and return 200")
  void shouldGetProductByIdAndReturn200() throws Exception {
    when(productService.getProductById("prod-1")).thenReturn(productDetailResponse);

    mockMvc
        .perform(get("/api/v1/products/prod-1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("prod-1"))
        .andExpect(jsonPath("$.name").value("Notebook Dell Inspiron"));
  }

  @Test
  @DisplayName("Should return 404 when product not found")
  void shouldReturn404WhenProductNotFound() throws Exception {
    when(productService.getProductById("invalid-id"))
        .thenThrow(new ResourceNotFoundException("Product not found"));

    mockMvc.perform(get("/api/v1/products/invalid-id")).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should create product and return 201")
  void shouldCreateProductAndReturn201() throws Exception {
    when(productService.createProduct(any(ProductRequest.class))).thenReturn(productBaseResponse);

    mockMvc
        .perform(
            post("/api/v1/products")
                .with(SecurityMockMvcRequestPostProcessors.user("admin@fastbuy.com").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value("prod-1"))
        .andExpect(jsonPath("$.name").value("Notebook Dell Inspiron"));
  }

  @Test
  @DisplayName("Should return 404 when category not found on create")
  void shouldReturn404WhenCategoryNotFoundOnCreate() throws Exception {
    when(productService.createProduct(any(ProductRequest.class)))
        .thenThrow(new ResourceNotFoundException("One or more categories not found"));

    mockMvc
        .perform(
            post("/api/v1/products")
                .with(SecurityMockMvcRequestPostProcessors.user("admin@fastbuy.com").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequest)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should update product and return 200")
  void shouldUpdateProductAndReturn200() throws Exception {
    when(productService.updateProduct(eq("prod-1"), any(ProductRequest.class)))
        .thenReturn(productBaseResponse);

    mockMvc
        .perform(
            put("/api/v1/products/prod-1")
                .with(SecurityMockMvcRequestPostProcessors.user("admin@fastbuy.com").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("prod-1"));
  }

  @Test
  @DisplayName("Should return 404 when product not found on update")
  void shouldReturn404WhenProductNotFoundOnUpdate() throws Exception {
    when(productService.updateProduct(eq("invalid-id"), any(ProductRequest.class)))
        .thenThrow(new ResourceNotFoundException("Product not found"));

    mockMvc
        .perform(
            put("/api/v1/products/invalid-id")
                .with(SecurityMockMvcRequestPostProcessors.user("admin@fastbuy.com").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequest)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should deactivate product and return 200")
  void shouldDeactivateProductAndReturn200() throws Exception {
    when(productService.deleteProduct("prod-1")).thenReturn(productBaseResponse);

    mockMvc
        .perform(
            delete("/api/v1/products/prod-1")
                .with(
                    SecurityMockMvcRequestPostProcessors.user("admin@fastbuy.com").roles("ADMIN")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("prod-1"))
        .andExpect(jsonPath("$.active").value(false));
  }

  @Test
  @DisplayName("Should return 404 when product not found on delete")
  void shouldReturn404WhenProductNotFoundOnDelete() throws Exception {
    when(productService.deleteProduct("invalid-id"))
        .thenThrow(new ResourceNotFoundException("Product not found"));

    mockMvc
        .perform(
            delete("/api/v1/products/invalid-id")
                .with(
                    SecurityMockMvcRequestPostProcessors.user("admin@fastbuy.com").roles("ADMIN")))
        .andExpect(status().isNotFound());
  }
}
