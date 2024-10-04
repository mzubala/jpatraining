package pl.com.bottega.jpatraining.locking;

import jakarta.persistence.EntityManager;

import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;

public class OptimisticInventoryUpdater implements InventoryUpdater {

    private final EntityManager em;

    public OptimisticInventoryUpdater(EntityManager em) {
        this.em = em;
    }

    @Override
    public void buy(String skuCode, Integer count) {
        var inventory = em.find(Inventory.class, skuCode);
        inventory.dec(count);
        em.persist(new InventoryTx(skuCode, -count));
    }

    @Override
    public void fill(String skuCode, Integer count) {
        var inventory = em.find(Inventory.class, skuCode);
        inventory.inc(count);
        em.persist(new InventoryTx(skuCode, count));
    }

}
