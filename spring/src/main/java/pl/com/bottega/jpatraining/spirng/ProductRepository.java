package pl.com.bottega.jpatraining.spirng;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

interface ProductRepository extends JpaRepository<Product, UUID> {

    @Override
    @Query("SELECT p FROM Product p JOIN FETCH p.categories")
    List<Product> findAll();
}
