package dev.nathanyan.fastbuy.category.service;

import dev.nathanyan.fastbuy.category.dto.CategoryNameResponse;
import dev.nathanyan.fastbuy.category.dto.CategoryRequest;
import dev.nathanyan.fastbuy.category.specification.CategorySpecification;
import dev.nathanyan.fastbuy.shared.entity.CategoryEntity;
import dev.nathanyan.fastbuy.shared.exception.CategoryAlreadyExistsException;
import dev.nathanyan.fastbuy.shared.exception.ResourceNotFoundException;
import dev.nathanyan.fastbuy.shared.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

  private final CategoryRepository categoryRepository;

  @Transactional
  public CategoryNameResponse createCategory(CategoryRequest request) {
    categoryRepository
        .findByName(request.name())
        .ifPresent(
            category -> {
              throw new CategoryAlreadyExistsException("Category already exists");
            });

    CategoryEntity category = new CategoryEntity();
    category.setName(request.name());
    categoryRepository.save(category);
    return CategoryNameResponse.from(category);
  }

  public List<CategoryNameResponse> getAllCategories() {
    return categoryRepository.findAll().stream().map(CategoryNameResponse::from).toList();
  }

  public CategoryNameResponse getCategoryById(String id) {
    return categoryRepository
        .findById(id)
        .map(CategoryNameResponse::from)
        .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
  }

  public List<CategoryNameResponse> searchCategories(String name) {
    Specification<CategoryEntity> spec = CategorySpecification.hasName(name);
    return categoryRepository.findAll(spec).stream().map(CategoryNameResponse::from).toList();
  }

  @Transactional
  public void deleteCategory(String id) {
    categoryRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    categoryRepository.deleteById(id);
  }

  @Transactional
  public CategoryNameResponse updateCategory(CategoryRequest request, String id) {
    CategoryEntity updatedCategory =
        categoryRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

    updatedCategory.setName(request.name());
    categoryRepository.save(updatedCategory);
    return CategoryNameResponse.from(updatedCategory);
  }
}
