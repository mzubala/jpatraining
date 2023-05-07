package pl.com.bottega.jpatraining.associations;

import jakarta.persistence.EntityManager;

public class AddProductToOrder {

    private final EntityManager em;

    public AddProductToOrder(EntityManager em) {
        this.em = em;
    }

    public void add(Long orderId, Long productId, Integer count) {
        // TODO - add product to order using just 1 DB query, hint: use EntityManager.getReference method
    }

}
