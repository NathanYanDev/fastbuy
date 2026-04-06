package dev.nathanyan.fastbuy.category.controller;

import dev.nathanyan.fastbuy.category.dto.CategoryNameResponse;
import dev.nathanyan.fastbuy.category.dto.CategoryRequest;
import dev.nathanyan.fastbuy.category.service.CategoryService;
import dev.nathanyan.fastbuy.security.ApiConstants;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiConstants.CATEGORY_PREFIX)
@RequiredArgsConstructor
public class CategoryController {
  private final CategoryService categoryService;

  @GetMapping
  public ResponseEntity<List<CategoryNameResponse>> getAllCategories() {
    return ResponseEntity.ok(categoryService.getAllCategories());
  }

  @GetMapping("/{id}")
  public ResponseEntity<CategoryNameResponse> getCategoryById(@PathVariable String id) {
    return ResponseEntity.ok(categoryService.getCategoryById(id));
  }

  @GetMapping("/search")
  public ResponseEntity<List<CategoryNameResponse>> searchCategories(
      @RequestParam(required = false) String name) {
    return ResponseEntity.ok(categoryService.searchCategories(name));
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<CategoryNameResponse> createCategory(@RequestBody CategoryRequest request) {
    CategoryNameResponse response = categoryService.createCategory(request);
    return ResponseEntity.created(URI.create(ApiConstants.CATEGORY_PREFIX + "/" + response.id()))
        .body(response);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<CategoryNameResponse> updateCategory(
      @PathVariable String id, @RequestBody CategoryRequest request) {
    CategoryNameResponse response = categoryService.updateCategory(request, id);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
    categoryService.deleteCategory(id);
    return ResponseEntity.noContent().build();
  }
}
