package dev.nathanyan.fastbuy.category.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.nathanyan.fastbuy.BaseControllerTest;
import dev.nathanyan.fastbuy.category.dto.CategoryNameResponse;
import dev.nathanyan.fastbuy.category.dto.CategoryRequest;
import dev.nathanyan.fastbuy.category.service.CategoryService;
import dev.nathanyan.fastbuy.shared.exception.CategoryAlreadyExistsException;
import dev.nathanyan.fastbuy.shared.exception.ResourceNotFoundException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest extends BaseControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockitoBean private CategoryService categoryService;

  private CategoryNameResponse categoryResponse;
  private CategoryRequest categoryRequest;

  @BeforeEach
  void setUp() {
    categoryResponse = new CategoryNameResponse("cat-1", "Notebooks");
    categoryRequest = new CategoryRequest("Notebooks");
  }

  @Test
  @DisplayName("Should get all categories and return 200")
  void shouldGetAllCategoriesAndReturn200() throws Exception {
    when(categoryService.getAllCategories()).thenReturn(List.of(categoryResponse));

    mockMvc
        .perform(get("/api/v1/categories"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value("cat-1"))
        .andExpect(jsonPath("$[0].name").value("Notebooks"));
  }

  @Test
  @DisplayName("Should return empty list when no categories found")
  void shouldReturnEmptyListWhenNoCategoriesFound() throws Exception {
    when(categoryService.getAllCategories()).thenReturn(List.of());

    mockMvc
        .perform(get("/api/v1/categories"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isEmpty());
  }

  @Test
  @DisplayName("Should get category by id and return 200")
  void shouldGetCategoryByIdAndReturn200() throws Exception {
    when(categoryService.getCategoryById("cat-1")).thenReturn(categoryResponse);

    mockMvc
        .perform(get("/api/v1/categories/cat-1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("cat-1"))
        .andExpect(jsonPath("$.name").value("Notebooks"));
  }

  @Test
  @DisplayName("Should return 404 when category not found")
  void shouldReturn404WhenCategoryNotFound() throws Exception {
    when(categoryService.getCategoryById("invalid-id"))
        .thenThrow(new ResourceNotFoundException("Category not found"));

    mockMvc.perform(get("/api/v1/categories/invalid-id")).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should search categories and return 200")
  void shouldSearchCategoriesAndReturn200() throws Exception {
    when(categoryService.searchCategories("Note")).thenReturn(List.of(categoryResponse));

    mockMvc
        .perform(get("/api/v1/categories/search").param("name", "Note"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Notebooks"));
  }

  @Test
  @DisplayName("Should search categories without filter and return 200")
  void shouldSearchCategoriesWithoutFilterAndReturn200() throws Exception {
    when(categoryService.searchCategories(null)).thenReturn(List.of(categoryResponse));

    mockMvc
        .perform(get("/api/v1/categories/search"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Notebooks"));
  }

  @Test
  @DisplayName("Should create category and return 201")
  void shouldCreateCategoryAndReturn201() throws Exception {
    when(categoryService.createCategory(any(CategoryRequest.class))).thenReturn(categoryResponse);

    mockMvc
        .perform(
            post("/api/v1/categories")
                .with(user("admin@fastbuy.com").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value("cat-1"))
        .andExpect(jsonPath("$.name").value("Notebooks"));
  }

  @Test
  @DisplayName("Should return 400 when category already exists")
  void shouldReturn400WhenCategoryAlreadyExists() throws Exception {
    when(categoryService.createCategory(any(CategoryRequest.class)))
        .thenThrow(new CategoryAlreadyExistsException("Category already exists"));

    mockMvc
        .perform(
            post("/api/v1/categories")
                .with(user("admin@fastbuy.com").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should update category and return 200")
  void shouldUpdateCategoryAndReturn200() throws Exception {
    CategoryRequest updateRequest = new CategoryRequest("Notebooks e Ultrabooks");
    CategoryNameResponse updatedResponse =
        new CategoryNameResponse("cat-1", "Notebooks e Ultrabooks");

    when(categoryService.updateCategory(any(CategoryRequest.class), eq("cat-1")))
        .thenReturn(updatedResponse);

    mockMvc
        .perform(
            put("/api/v1/categories/cat-1")
                .with(user("admin@fastbuy.com").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Notebooks e Ultrabooks"));
  }

  @Test
  @DisplayName("Should return 404 when category not found on update")
  void shouldReturn404WhenCategoryNotFoundOnUpdate() throws Exception {
    when(categoryService.updateCategory(any(CategoryRequest.class), eq("invalid-id")))
        .thenThrow(new ResourceNotFoundException("Category not found"));

    mockMvc
        .perform(
            put("/api/v1/categories/invalid-id")
                .with(user("admin@fastbuy.com").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequest)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should delete category and return 204")
  void shouldDeleteCategoryAndReturn204() throws Exception {
    doNothing().when(categoryService).deleteCategory("cat-1");

    mockMvc
        .perform(delete("/api/v1/categories/cat-1").with(user("admin@fastbuy.com").roles("ADMIN")))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("Should return 404 when category not found on delete")
  void shouldReturn404WhenCategoryNotFoundOnDelete() throws Exception {
    doThrow(new ResourceNotFoundException("Category not found"))
        .when(categoryService)
        .deleteCategory("invalid-id");

    mockMvc
        .perform(
            delete("/api/v1/categories/invalid-id").with(user("admin@fastbuy.com").roles("ADMIN")))
        .andExpect(status().isNotFound());
  }
}
