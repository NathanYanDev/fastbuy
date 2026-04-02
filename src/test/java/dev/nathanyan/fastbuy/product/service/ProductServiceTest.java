package dev.nathanyan.fastbuy.product.service;

import dev.nathanyan.fastbuy.product.dto.ProductBaseResponse;
import dev.nathanyan.fastbuy.product.dto.ProductDetailResponse;
import dev.nathanyan.fastbuy.product.dto.ProductRequest;
import dev.nathanyan.fastbuy.product.dto.ProductSummaryResponse;
import dev.nathanyan.fastbuy.shared.entity.*;
import dev.nathanyan.fastbuy.shared.exception.ResourceNotFoundException;
import dev.nathanyan.fastbuy.shared.repository.CategoryRepository;
import dev.nathanyan.fastbuy.shared.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

  @InjectMocks private ProductService productService;
  @Mock private ProductRepository productRepository;
  @Mock private CategoryRepository categoryRepository;

  private ProductEntity product;
  private CategoryEntity category;
  private ProductRequest productRequest;
  private ProductVariantEntity variant;

  @BeforeEach
  void setUp() {
    category = CategoryEntity.builder().id("cat-1").name("Notebooks").build();

    ProductCategoryEntity productCategory =
        ProductCategoryEntity.builder().product(product).category(category).build();

    ProductImageEntity coverImage =
        ProductImageEntity.builder()
            .id("img-1")
            .url("https://cloudinary.com/image.jpg")
            .isCover(true)
            .displayOrder(0)
            .build();

    ProductDimensionEntity dimension =
        ProductDimensionEntity.builder().width(10.0).height(10.0).depth(10.0).weight(10.0).build();

    ProductVariantEntity variant =
        ProductVariantEntity.builder()
            .id("var-1")
            .name("Dell Inspiron 15 - i7 16GB")
            .sku("DELL-i7-16GB")
            .price(new BigDecimal("4599.90"))
            .currency("BRL")
            .stockQuantity(10)
            .active(true)
            .defaultVariant(true)
            .images(new ArrayList<>(List.of(coverImage)))
            .dimensions(dimension)
            .details(new HashMap<>())
            .build();

    product =
        ProductEntity.builder()
            .id("prod-1")
            .name("Notebook Dell Inspiron")
            .description("Notebook para uso profissional")
            .isActive(true)
            .variants(new ArrayList<>(List.of(variant)))
            .categories(new ArrayList<>(List.of(productCategory)))
            .build();

    productRequest =
        new ProductRequest(
            "Notebook Dell Inspiron", "Notebook para uso profissional", List.of("cat-1"));
  }

  @Test
  @DisplayName("Should get all active products successfully")
  void shouldGetAllActiveProductsSuccessfully() {
    Page<ProductEntity> productPage = new PageImpl<>(List.of(product));

    when(productRepository.findAllByIsActiveTrue(any(Pageable.class))).thenReturn(productPage);

    Page<ProductSummaryResponse> result = productService.getAllProducts(0, 20, "createdAt", "desc");

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    verify(productRepository, times(1)).findAllByIsActiveTrue(any(Pageable.class));
  }

  @Test
  @DisplayName("Should limit page size to 50")
  void shouldLimitPageSizeTo50() {
    Page<ProductEntity> productPage = new PageImpl<>(List.of());

    when(productRepository.findAllByIsActiveTrue(any(Pageable.class))).thenReturn(productPage);

    productService.getAllProducts(0, 100, "createdAt", "desc");

    ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
    verify(productRepository).findAllByIsActiveTrue(captor.capture());
    assertEquals(50, captor.getValue().getPageSize());
  }

  @Test
  @DisplayName("Should fallback to createdAt when sortBy is invalid")
  void shouldFallbackToCreatedAtWhenSortByIsInvalid() {
    Page<ProductEntity> productPage = new PageImpl<>(List.of());

    when(productRepository.findAllByIsActiveTrue(any(Pageable.class))).thenReturn(productPage);

    productService.getAllProducts(0, 20, "invalidField", "desc");

    ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
    verify(productRepository).findAllByIsActiveTrue(captor.capture());
    assertEquals("createdAt", captor.getValue().getSort().iterator().next().getProperty());
  }

  @Test
  @DisplayName("Should sort ascending when direction is asc")
  void shouldSortAscendingWhenDirectionIsAsc() {
    Page<ProductEntity> productPage = new PageImpl<>(List.of());

    when(productRepository.findAllByIsActiveTrue(any(Pageable.class))).thenReturn(productPage);

    productService.getAllProducts(0, 20, "name", "asc");

    ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
    verify(productRepository).findAllByIsActiveTrue(captor.capture());
    assertEquals(Sort.Direction.ASC, captor.getValue().getSort().iterator().next().getDirection());
  }

  @Test
  @DisplayName("Should get product by id successfully")
  void shouldGetProductByIdSuccessfully() {
    when(productRepository.findById("prod-1")).thenReturn(Optional.of(product));

    ProductDetailResponse response = productService.getProductById("prod-1");

    assertNotNull(response);
    assertEquals("prod-1", response.id());
  }

  @Test
  @DisplayName("Should throw exception when product not found")
  void shouldThrowExceptionWhenProductNotFound() {
    when(productRepository.findById("invalid-id")).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class, () -> productService.getProductById("invalid-id"));
  }

  @Test
  @DisplayName("Should create product successfully")
  void shouldCreateProductSuccessfully() {
    when(categoryRepository.findAllById(List.of("cat-1"))).thenReturn(List.of(category));
    when(productRepository.save(any())).thenReturn(product);

    ProductBaseResponse response = productService.createProduct(productRequest);

    assertNotNull(response);
    verify(productRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("Should throw exception when category not found on createProduct")
  void shouldThrowExceptionWhenCategoryNotFound() {
    when(categoryRepository.findAllById(List.of("cat-1"))).thenReturn(List.of());

    assertThrows(
        ResourceNotFoundException.class, () -> productService.createProduct(productRequest));

    verify(productRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should link categories to product on create")
  void shouldLinkCategoriesToProductOnCreate() {
    when(categoryRepository.findAllById(List.of("cat-1"))).thenReturn(List.of(category));
    when(productRepository.save(any())).thenReturn(product);

    productService.createProduct(productRequest);

    ArgumentCaptor<ProductEntity> captor = ArgumentCaptor.forClass(ProductEntity.class);
    verify(productRepository).save(captor.capture());
    assertEquals(1, captor.getValue().getCategories().size());
  }

  @Test
  @DisplayName("Should throw exception when partial categories not found")
  void shouldThrowExceptionWhenPartialCategoriesNotFound() {
    ProductRequest requestWithTwoCategories =
        new ProductRequest("Notebook Dell", "Descrição", List.of("cat-1", "cat-2"));

    when(categoryRepository.findAllById(any())).thenReturn(List.of(category)); // retorna só 1 de 2

    assertThrows(
        ResourceNotFoundException.class,
        () -> productService.createProduct(requestWithTwoCategories));

    verify(productRepository, never()).save(any());
  }
}
