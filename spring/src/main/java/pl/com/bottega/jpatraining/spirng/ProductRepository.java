package pl.com.bottega.jpatraining.spirng;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

import static pl.com.bottega.jpatraining.spirng.Product_.productName;

interface ProductRepository extends JpaRepository<Product, UUID> {

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.attributes")
    List<Product> findAllWithAttributes();

}

class ProductSpecifications {
    static Specification<Product> byPhrase(String phrase) {
        return (product, query, criteriaBuilder) ->
            criteriaBuilder.like(criteriaBuilder.lower(product.get(productName)), "%" + phrase.toLowerCase() + "%");
    }
}
