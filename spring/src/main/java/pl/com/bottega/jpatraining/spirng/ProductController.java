package pl.com.bottega.jpatraining.spirng;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityManager;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Log
public class ProductController {

    private final EntityManager entityManager;
    private final ProductRepository repository;
    private final ProductService productService;

    @GetMapping
    public List<Product> getProduct() {
        var products = productService.getAll();
        return products;
    }

    @Transactional
    @PostMapping
    void createProduct() {
        entityManager.persist(new Product());
    }

    @PostMapping("/2")
    public void createProduct2() {
        repository.save(new Product());
    }
}
