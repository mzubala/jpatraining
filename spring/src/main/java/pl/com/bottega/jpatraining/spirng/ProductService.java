package pl.com.bottega.jpatraining.spirng;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class ProductService {

    private final ProductRepository productRepository;
    private final AttributeRepository attributeRepository;

    private final TransactionTemplate transactionTemplate;

    void create(ProductDataRequest product) {
        fetchData();
        transactionTemplate.executeWithoutResult((__) -> doCreate(product));
    }

    @SneakyThrows
    private void fetchData() {
        Thread.sleep(500);
    }

    void doCreate(ProductDataRequest product) {
        productRepository.save(new Product(
            product.name(),
            product.attributeIds().stream().map(attributeRepository::getReferenceById)
                .collect(Collectors.toSet())
        ));
    }

    @Transactional(readOnly = true)
    List<Product> getProducts() {
        return productRepository.findAllWithAttributes();
    }

    public Product getProduct(UUID id) {
        return productRepository.findById(id).get();
    }
}

record ProductDataRequest(String name, Set<UUID> attributeIds) {}

