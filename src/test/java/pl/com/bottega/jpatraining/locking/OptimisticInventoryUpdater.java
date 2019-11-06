package pl.com.bottega.jpatraining.locking;

import javax.persistence.EntityManager;

public class OptimisticInventoryUpdater implements InventoryUpdater {

    private final EntityManager em;

    public OptimisticInventoryUpdater(EntityManager entityManager) {
        this.em = entityManager;
    }

    @Override
    public void buy(String skuCode, Integer count) {
        Inventory inventory = em.find(Inventory.class, skuCode);
        inventory.dec(count);
        em.persist(new InventoryTx(skuCode, -count));
    }

    @Override
    public void fill(String skuCode, Integer count) {
        Inventory inventory = em.find(Inventory.class, skuCode);
        inventory.inc(count);
        em.persist(new InventoryTx(skuCode, count));
    }

}
