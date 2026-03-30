package dev.nathanyan.fastbuy.product.service;

import dev.nathanyan.fastbuy.product.dto.ProductSummaryResponse;
import dev.nathanyan.fastbuy.shared.repository.ProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductService {
  private static final List<String> ALLOWED_SORT_FIELDS = List.of("name", "price", "createdAt");
  private final ProductRepository productRepository;

  public Page<ProductSummaryResponse> getAllProducts(
      int page, int size, String sortBy, String direction) {
    int validatedSize = Math.min(size, 50);
    Pageable pageable = PageRequest.of(page, validatedSize, buildSort(sortBy, direction));
    return productRepository.findAllByActiveTrue(pageable).map(ProductSummaryResponse::from);
  }

  private Sort buildSort(String sortBy, String direction) {
    if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
      sortBy = "createdAt";
    }
    return direction.equalsIgnoreCase("asc")
        ? Sort.by(sortBy).ascending()
        : Sort.by(sortBy).descending();
  }

  public ProductSummaryResponse getProductById(String id) {
    return productRepository.findById(id).map(ProductSummaryResponse::from).orElse(null);
  }

  public ProductSummaryResponse createProduct(ProductSummaryResponse product) {
    // TODO: implement
    return null;
  }
}
