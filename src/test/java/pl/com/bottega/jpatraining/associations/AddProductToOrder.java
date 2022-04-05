package pl.com.bottega.jpatraining.associations;

import javax.persistence.EntityManager;

public class AddProductToOrder {

    private final EntityManager em;

    public AddProductToOrder(EntityManager em) {
        this.em = em;
    }

    public void add(Long orderId, Long productId, Integer count) {
        Order order = em.getReference(Order.class, orderId);
        Product product = em.getReference(Product.class, productId);
        em.persist(new LineItem(order, product, count));
    }

}
