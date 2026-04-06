package dev.nathanyan.fastbuy.category.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.nathanyan.fastbuy.category.dto.CategoryNameResponse;
import dev.nathanyan.fastbuy.category.dto.CategoryRequest;
import dev.nathanyan.fastbuy.shared.entity.CategoryEntity;
import dev.nathanyan.fastbuy.shared.exception.CategoryAlreadyExistsException;
import dev.nathanyan.fastbuy.shared.exception.ResourceNotFoundException;
import dev.nathanyan.fastbuy.shared.repository.CategoryRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

  private final String categoryId = "cat-1";
  @InjectMocks private CategoryService categoryService;
  @Mock private CategoryRepository categoryRepository;
  private CategoryEntity category;
  private CategoryRequest categoryRequest;

  @BeforeEach
  void setUp() {
    category = new CategoryEntity();
    category.setId(categoryId);
    category.setName("Notebooks");

    categoryRequest = new CategoryRequest("Notebooks");
  }

  @Test
  @DisplayName("Should get all categories successfully")
  void shouldGetAllCategoriesSuccessfully() {
    when(categoryRepository.findAll()).thenReturn(List.of(category));

    List<CategoryNameResponse> response = categoryService.getAllCategories();

    assertNotNull(response);
    assertEquals(1, response.size());
    assertEquals("Notebooks", response.get(0).name());
    verify(categoryRepository, times(1)).findAll();
  }

  @Test
  @DisplayName("Should return empty list when no categories found")
  void shouldReturnEmptyListWhenNoCategoriesFound() {
    when(categoryRepository.findAll()).thenReturn(List.of());

    List<CategoryNameResponse> response = categoryService.getAllCategories();

    assertNotNull(response);
    assertTrue(response.isEmpty());
  }

  @Test
  @DisplayName("Should get category by id successfully")
  void shouldGetCategoryByIdSuccessfully() {
    when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

    CategoryNameResponse response = categoryService.getCategoryById(categoryId);

    assertNotNull(response);
    assertEquals("Notebooks", response.name());
  }

  @Test
  @DisplayName("Should throw exception when category not found on getById")
  void shouldThrowExceptionWhenCategoryNotFoundOnGetById() {
    when(categoryRepository.findById("invalid-id")).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class, () -> categoryService.getCategoryById("invalid-id"));
  }

  @Test
  @DisplayName("Should search categories by name successfully")
  void shouldSearchCategoriesByNameSuccessfully() {
    when(categoryRepository.findAll(any(Specification.class))).thenReturn(List.of(category));

    List<CategoryNameResponse> response = categoryService.searchCategories("Note");

    assertNotNull(response);
    assertEquals(1, response.size());
    verify(categoryRepository, times(1)).findAll(any(Specification.class));
  }

  @Test
  @DisplayName("Should return all categories when name is null on search")
  void shouldReturnAllCategoriesWhenNameIsNullOnSearch() {
    when(categoryRepository.findAll(any(Specification.class))).thenReturn(List.of(category));

    List<CategoryNameResponse> response = categoryService.searchCategories(null);

    assertNotNull(response);
    assertEquals(1, response.size());
  }

  @Test
  @DisplayName("Should create category successfully")
  void shouldCreateCategorySuccessfully() {
    when(categoryRepository.findByName("Notebooks")).thenReturn(Optional.empty());
    when(categoryRepository.save(any())).thenReturn(category);

    CategoryNameResponse response = categoryService.createCategory(categoryRequest);

    assertNotNull(response);
    assertEquals("Notebooks", response.name());
    verify(categoryRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("Should throw exception when category already exists")
  void shouldThrowExceptionWhenCategoryAlreadyExists() {
    when(categoryRepository.findByName("Notebooks")).thenReturn(Optional.of(category));

    assertThrows(
        CategoryAlreadyExistsException.class,
        () -> categoryService.createCategory(categoryRequest));

    verify(categoryRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should update category successfully")
  void shouldUpdateCategorySuccessfully() {
    CategoryRequest updateRequest = new CategoryRequest("Notebooks e Ultrabooks");

    when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
    when(categoryRepository.save(any())).thenReturn(category);

    CategoryNameResponse response = categoryService.updateCategory(updateRequest, categoryId);

    assertNotNull(response);
    ArgumentCaptor<CategoryEntity> captor = ArgumentCaptor.forClass(CategoryEntity.class);
    verify(categoryRepository).save(captor.capture());
    assertEquals("Notebooks e Ultrabooks", captor.getValue().getName());
  }

  @Test
  @DisplayName("Should throw exception when category not found on update")
  void shouldThrowExceptionWhenCategoryNotFoundOnUpdate() {
    when(categoryRepository.findById("invalid-id")).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> categoryService.updateCategory(categoryRequest, "invalid-id"));

    verify(categoryRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should delete category successfully")
  void shouldDeleteCategorySuccessfully() {
    when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

    categoryService.deleteCategory(categoryId);

    verify(categoryRepository, times(1)).deleteById(categoryId);
  }

  @Test
  @DisplayName("Should throw exception when category not found on delete")
  void shouldThrowExceptionWhenCategoryNotFoundOnDelete() {
    when(categoryRepository.findById("invalid-id")).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class, () -> categoryService.deleteCategory("invalid-id"));

    verify(categoryRepository, never()).deleteById(any());
  }
}
