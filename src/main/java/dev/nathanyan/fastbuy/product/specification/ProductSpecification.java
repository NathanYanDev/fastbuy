package dev.nathanyan.fastbuy.product.specification;

import dev.nathanyan.fastbuy.shared.entity.ProductEntity;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {
  public static Specification<ProductEntity> baseSpec() {
    return isActive().and(hasVariants());
  }

  public static Specification<ProductEntity> hasName(String name) {
    return (root, query, cb) ->
        name == null ? null : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
  }

  public static Specification<ProductEntity> hasCategory(String categoryId) {
    return (root, query, cb) ->
        categoryId == null
            ? null
            : cb.equal(root.join("categories").join("category").get("id"), categoryId);
  }

  public static Specification<ProductEntity> isActive() {
    return (root, query, cb) -> cb.isTrue(root.get("isActive"));
  }

  public static Specification<ProductEntity> hasVariants() {
    return (root, query, cb) -> cb.isNotEmpty(root.get("variants"));
  }
}
