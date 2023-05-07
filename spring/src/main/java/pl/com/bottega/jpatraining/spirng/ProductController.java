package pl.com.bottega.jpatraining.spirng;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityManager;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Log
public class ProductController {

    private final EntityManager entityManager;

    @GetMapping
    public void getProduct() {
        log.info(entityManager.getClass().toString());
    }
}
