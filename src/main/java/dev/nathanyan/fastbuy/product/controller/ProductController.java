package dev.nathanyan.fastbuy.product.controller;

import dev.nathanyan.fastbuy.product.dto.ProductSummaryResponse;
import dev.nathanyan.fastbuy.product.service.ProductService;
import dev.nathanyan.fastbuy.security.ApiConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(ApiConstants.PRODUCT_PREFIX)
public class ProductController {
  private final ProductService productService;

  @GetMapping
  public ResponseEntity<Page<ProductSummaryResponse>> listProducts(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String direction) {

    return ResponseEntity.ok(productService.getAllProducts(page, size, sortBy, direction));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProductSummaryResponse> getProductById(@PathVariable String id) {
    return ResponseEntity.ok(productService.getProductById(id));
  }
}
