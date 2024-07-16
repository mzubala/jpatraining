package pl.com.bottega.jpatraining.spirng;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    void updateProduct(Long productId) {
        var product = productRepository.findById(productId).orElseThrow();
        product.setName("Updated name");
        //productRepository.save(product);
    }

}
