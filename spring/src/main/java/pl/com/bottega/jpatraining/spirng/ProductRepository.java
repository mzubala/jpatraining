package pl.com.bottega.jpatraining.spirng;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

    List<Product> findByNameLike(String pattern);

    @Query(value = "SELECT p FROM Product p WHERE p.name LIKE :pattern")
    List<Product> x(String pattern);

    @Query(name = "y")
    List<Product> y(String pattern);

}
