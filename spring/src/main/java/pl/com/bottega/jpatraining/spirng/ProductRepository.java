package pl.com.bottega.jpatraining.spirng;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

interface ProductRepository extends Repository<Product, Long> {

    void save(Product product);

    @Query("select p from Product p LEFT JOIN FETCH p.categories")
    List<Product> findAll();

    Optional<Product> findById(Long productId);

    Product getReferenceById(Long id);
}
