package pl.com.bottega.jpatraining.spirng;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public List<Product> getAll() {
        return productRepository.findAll().stream().map(p -> {
            p.getCategories().size();
            return p;
        }).collect(Collectors.toList());
    }
}
