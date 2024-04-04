package pl.com.bottega.jpatraining.spirng;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static pl.com.bottega.jpatraining.spirng.ProductSpecificationFactory.byCategoryId;
import static pl.com.bottega.jpatraining.spirng.ProductSpecificationFactory.byNameLike;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Log
public class ProductController {

    private final EntityManager entityManager;
    private final TransactionTemplate transactionTemplate;
    private final ProductRepository productRepository;

    @GetMapping
    public void getProduct() {
        log.info(entityManager.getClass().toString());
    }

    @PostMapping("/em")
    @Transactional
    public Product createProductWithEm() {
        var p = new Product();
        entityManager.persist(p);
        return p;
    }

    @PostMapping("/tt")
    public void createProductWithTt() {
        transactionTemplate.executeWithoutResult((c) -> {
            entityManager.persist(new Product());
        });
    }

    @PostMapping("/r")
    public void createProductWithRepo() {
        productRepository.save(new Product());
    }

    @PutMapping("/{id}")
    @Transactional
    public void update(@PathVariable UUID id) {
        productRepository.findById(id).ifPresent(p -> {
            p.name = "Nowa nazwa";
        });
    }

    @GetMapping("/{id}")
    //@Transactional(readOnly = true)
    public Product getById(@PathVariable UUID id) {
        var p = productRepository.findById(id).get();
        p.getCategories().size();
        return p;
    }

    @GetMapping("/search")
    public List<Product> getProducts() {
        return productRepository.findAll(byNameLike("%Test%").or(byCategoryId(50L)));
    }
}
