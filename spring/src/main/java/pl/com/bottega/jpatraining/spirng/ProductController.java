package pl.com.bottega.jpatraining.spirng;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Log
public class ProductController {

    private final ProductRepository productRepository;

    private final AuctionRepository auctionRepository;

    private final TransactionTemplate transactionTemplate;

    private final ProductService productService;

    @PostMapping
    @Transactional
    public Product createProduct() {
        var product = new Product(Set.of(new Category(), new Category(), new Category()));
        productRepository.save(product);
        return product;
    }

    @PostMapping("/tt")
    public void createProductTT() {
        transactionTemplate.executeWithoutResult((status) -> {
            auctionRepository.save(new Auction(UUID.randomUUID().toString()));
        });
    }

    @GetMapping("/{id}")
    public ProductDto getProduct(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    @PutMapping("/{id}")
    public void updateProduct(@PathVariable Long id) {
        productService.updateProduct(id);
    }

    @GetMapping
    public List<ProductDto> getProducts() {
        return productService.getProducts();
    }

}
