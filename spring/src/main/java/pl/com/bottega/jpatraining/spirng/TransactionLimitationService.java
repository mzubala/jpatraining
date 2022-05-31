package pl.com.bottega.jpatraining.spirng;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;

@Component
@RequiredArgsConstructor
public class TransactionLimitationService {

    private final EntityManager entityManager;

    private final TransactionTemplate transactionTemplate;

    public void createProduct() {
        saveInTx();
    }

    private void saveInTx() {
        transactionTemplate.executeWithoutResult((callback) -> {
            entityManager.persist(new Product("Sample"));
        });
    }
}
