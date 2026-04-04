package dev.nathanyan.fastbuy.product.service;

import dev.nathanyan.fastbuy.product.dto.ProductBaseResponse;
import dev.nathanyan.fastbuy.product.dto.ProductDetailResponse;
import dev.nathanyan.fastbuy.product.dto.ProductRequest;
import dev.nathanyan.fastbuy.product.dto.ProductSummaryResponse;
import dev.nathanyan.fastbuy.product.specification.ProductSpecification;
import dev.nathanyan.fastbuy.shared.entity.CategoryEntity;
import dev.nathanyan.fastbuy.shared.entity.ProductCategoryEntity;
import dev.nathanyan.fastbuy.shared.entity.ProductEntity;
import dev.nathanyan.fastbuy.shared.exception.ResourceNotFoundException;
import dev.nathanyan.fastbuy.shared.repository.CategoryRepository;
import dev.nathanyan.fastbuy.shared.repository.ProductRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductService {
  private static final List<String> ALLOWED_SORT_FIELDS = List.of("name", "price", "createdAt");
  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;

  public Page<ProductSummaryResponse> getAllProducts(
      int page, int size, String sortBy, String direction) {
    int validatedSize = Math.min(size, 50);
    Pageable pageable = PageRequest.of(page, validatedSize, buildSort(sortBy, direction));
    return productRepository
        .findAll(ProductSpecification.baseSpec(), pageable)
        .map(ProductSummaryResponse::from);
  }

  public ProductDetailResponse getProductById(String id) {
    return productRepository
        .findById(id)
        .map(ProductDetailResponse::from)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
  }

  public Page<ProductSummaryResponse> searchProducts(
      String name, String categoryId, int page, int size, String sortBy, String direction) {
    int validatedSize = Math.min(size, 50);
    Pageable pageable = PageRequest.of(page, validatedSize, buildSort(sortBy, direction));
    Specification<ProductEntity> spec =
        ProductSpecification.baseSpec()
            .and(ProductSpecification.hasName(name))
            .and(ProductSpecification.hasCategory(categoryId));

    return productRepository.findAll(spec, pageable).map(ProductSummaryResponse::from);
  }

  @Transactional
  public ProductBaseResponse createProduct(ProductRequest request) {
    List<CategoryEntity> categories = categoryRepository.findAllById(request.categoryIds());

    if (categories.size() != request.categoryIds().size()) {
      throw new ResourceNotFoundException("One or more categories not found");
    }

    ProductEntity product =
        ProductEntity.builder()
            .name(request.name())
            .description(request.description())
            .isActive(false)
            .build();

    List<ProductCategoryEntity> productCategories =
        categories.stream()
            .map(
                category ->
                    ProductCategoryEntity.builder().product(product).category(category).build())
            .toList();

    product.setCategories(productCategories);

    productRepository.save(product);

    return ProductBaseResponse.from(product);
  }

  @Transactional
  public ProductBaseResponse updateProduct(String id, ProductRequest request) {
    ProductEntity updatedProduct =
        productRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    List<CategoryEntity> categories = categoryRepository.findAllById(request.categoryIds());

    if (categories.size() != request.categoryIds().size()) {
      throw new ResourceNotFoundException("One or more categories not found");
    }

    updatedProduct.setName(request.name());
    updatedProduct.setDescription(request.description());

    updatedProduct.getCategories().clear();

    List<ProductCategoryEntity> newCategories =
        categories.stream()
            .map(
                category ->
                    ProductCategoryEntity.builder()
                        .product(updatedProduct)
                        .category(category)
                        .build())
            .toList();

    updatedProduct.setCategories(newCategories);

    productRepository.save(updatedProduct);

    return ProductBaseResponse.from(updatedProduct);
  }

  @Transactional
  public ProductBaseResponse deleteProduct(String id) {
    ProductEntity product =
        productRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    product.setIsActive(false);
    productRepository.save(product);
    return ProductBaseResponse.from(product);
  }

  private Sort buildSort(String sortBy, String direction) {
    if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
      sortBy = "createdAt";
    }
    return direction.equalsIgnoreCase("asc")
        ? Sort.by(sortBy).ascending()
        : Sort.by(sortBy).descending();
  }
}
