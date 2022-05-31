package pl.com.bottega.jpatraining.spirng;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Log
public class ProductController {

    private final ProductService productService;

    private final TransactionLimitationService transactionLimitationService;

    @PostMapping
    public void createProduct() {
        transactionLimitationService.createProduct();
    }

    @PutMapping
    public void updateProducts() {
        productService.updateProduct(1L, "ala");
        productService.updateProduct(2L, "ma");
        productService.updateProduct(3L, "kota");
    }

    @GetMapping
    public Product getProduct() {
        productService.getProduct();
        return productService.getProduct();
    }
}
