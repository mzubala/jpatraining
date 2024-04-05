package pl.com.bottega.jpatraining.spirng;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.jpa.AvailableHints;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hibernate.jpa.HibernateHints.HINT_READ_ONLY;

@Component
@RequiredArgsConstructor
class ProductService {

    private final EntityManager entityManager;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    ProductDto getProduct(Long id) {
        return toDto(entityManager.find(Product.class, id));
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getProducts() {
        return entityManager.createQuery("SELECT p FROM Product p LEFT JOIN FETCH p.categories", Product.class)
            .getResultList().stream().map(this::toDto).toList();
    }

    @Transactional
    public void createOrder() {
        var products = entityManager.createQuery("SELECT p FROM Product p LEFT JOIN FETCH p.categories", Product.class)
            .setHint(HINT_READ_ONLY, true)
            .getResultList();

        // create order
        // em.persist(order)
    }

    private ProductDto toDto(Product product) {
        return new ProductDto(product.getId(),
            product.getCategories().stream().map(c -> new CategoryDto(c.getId())).collect(Collectors.toSet()));
    }

    @Transactional
    public void updateProduct(Long id) {
        var product = productRepository.getReferenceById(id);
        product.setName("Name");
        product.getCategories().add(new Category());
        productRepository.save(product);
    }
}

record ProductDto(Long id, Set<CategoryDto> categories) {

}

record CategoryDto(Long id) {}
