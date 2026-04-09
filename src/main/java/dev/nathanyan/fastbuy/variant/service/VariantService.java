package dev.nathanyan.fastbuy.variant.service;

import dev.nathanyan.fastbuy.shared.entity.ProductEntity;
import dev.nathanyan.fastbuy.shared.exception.ResourceNotFoundException;
import dev.nathanyan.fastbuy.shared.repository.ProductRepository;
import dev.nathanyan.fastbuy.shared.repository.ProductVariantRepository;
import dev.nathanyan.fastbuy.variant.dto.VariantDetailResponse;
import dev.nathanyan.fastbuy.variant.dto.VariantRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class VariantService {
  private final ProductVariantRepository productVariantRepository;
  private final ProductRepository productRepository;

  public VariantDetailResponse createVariant(VariantRequest variantRequest) {
    ProductEntity product =
        productRepository
            .findById(variantRequest.productId())
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

    // TODO: Create variant

    product.setIsActive(true);
    productRepository.save(product);

    return null; // Temporary
  }

  public VariantDetailResponse setDefaultVariant() {
    return null;
  }
}
