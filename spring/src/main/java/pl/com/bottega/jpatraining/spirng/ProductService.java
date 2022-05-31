package pl.com.bottega.jpatraining.spirng;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Component
@RequiredArgsConstructor
public class ProductService {

    private final EntityManager entityManager;

    @Transactional
    public void createProduct() {
        entityManager.persist(new Product("Sample", new Category("Sample")));
    }

    @Transactional(readOnly = true)
    public Product getProduct() {
        var product1 = entityManager.find(Product.class, 1L);
        var product2 = entityManager.find(Product.class, 1L);
        return product2;
    }

    @Transactional
    public void updateProduct(Long id, String name) {
        var product = entityManager.find(Product.class, id);
        product.setName(name);
    }
}
