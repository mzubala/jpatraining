package pl.com.bottega.jpatraining.spirng;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

class ProductSpecificationFactory {

    static Specification<Product> byNameLike(String pattern) {
        return (product, query, criteriaBuilder) -> criteriaBuilder.like(product.get("name"), pattern);
    }

    static Specification<Product> byCategoryId(Long categoryId) {
        return (product, query, criteriaBuilder) -> criteriaBuilder.equal(product.get("categories").get("id"), categoryId);
    }

}
