package dev.nathanyan.fastbuy.category.specification;

import dev.nathanyan.fastbuy.shared.entity.CategoryEntity;
import org.springframework.data.jpa.domain.Specification;

public class CategorySpecification {
  public static Specification<CategoryEntity> hasName(String name) {
    return (root, query, cb) ->
        name == null ? null : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
  }
}
