package dev.nathanyan.fastbuy.category.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.nathanyan.fastbuy.BaseControllerTest;
import dev.nathanyan.fastbuy.category.dto.CategoryRequest;
import dev.nathanyan.fastbuy.category.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = CategoryController.class)
class CategoryControllerAuthorizationTest extends BaseControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockitoBean private CategoryService categoryService;

  private CategoryRequest categoryRequest;

  @BeforeEach
  void setUp() {
    categoryRequest = new CategoryRequest("Notebooks");
  }

  @Test
  @DisplayName("Should return 403 when creating category without admin role")
  void shouldReturn403WhenCreatingCategoryWithoutAdminRole() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/categories")
                .with(user("customer@email.com").roles("CUSTOMER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequest)))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("Should return 403 when updating category without admin role")
  void shouldReturn403WhenUpdatingCategoryWithoutAdminRole() throws Exception {
    mockMvc
        .perform(
            put("/api/v1/categories/cat-1")
                .with(user("customer@email.com").roles("CUSTOMER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequest)))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("Should return 403 when deleting category without admin role")
  void shouldReturn403WhenDeletingCategoryWithoutAdminRole() throws Exception {
    mockMvc
        .perform(
            delete("/api/v1/categories/cat-1").with(user("customer@email.com").roles("CUSTOMER")))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("Should return 403 when creating category without authentication")
  void shouldReturn403WhenCreatingCategoryWithoutAuthentication() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequest)))
        .andExpect(status().isForbidden());
  }
}
