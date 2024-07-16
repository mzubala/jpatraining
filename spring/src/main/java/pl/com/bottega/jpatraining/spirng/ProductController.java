package pl.com.bottega.jpatraining.spirng;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Log
public class ProductController {

    private final EntityManager entityManager;
    private final TransactionTemplate transactionTemplate;
    private final ProductRepository productRepository;
    private final ProductService productService;

    @GetMapping
    @Transactional(readOnly = true)
    public List<Product> getProducts() {
        return entityManager.createQuery("select p from Product p LEFT JOIN FETCH p.categories", Product.class).getResultList();
    }

    @GetMapping("/repo")
    public List<Product> getProductsFromRepository() {
        return productRepository.findAll();
    }

    @PostMapping
    @Transactional
    public void createProduct() {
        entityManager.persist(new Product("Test"));
    }

    @PostMapping("/repo")
    public void createProductInRepo() {
        productRepository.save(new Product("Test repo"));
    }

    @PostMapping("/tt")
    public void createProductWithTransactionTemplate() {
        // pobiarenie danych z REST
        transactionTemplate.executeWithoutResult((__) -> entityManager.persist(new Product("Test TT")));
        // zapis danych przez REST
    }

    @PutMapping("/{id}")
    public void updateProduct(@PathVariable Long id) {
        productService.updateProduct(id);
    }

}
