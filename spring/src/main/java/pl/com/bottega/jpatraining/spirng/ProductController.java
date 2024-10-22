package pl.com.bottega.jpatraining.spirng;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Log
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<Product> getProduct() {
        return productService.getProducts();
    }

    @GetMapping("/{id}")
    public Product getProduct(@PathVariable UUID id) {
        return productService.getProduct(id);
    }

    @PostMapping
    public void createProduct(@RequestBody ProductDataRequest product) {
        productService.create(product);
    }
}
