package pl.com.bottega.jpatraining.locking;

import javax.persistence.EntityManager;

public class OptimisticInventoryUpdater implements InventoryUpdater {

    private final EntityManager em;

    public OptimisticInventoryUpdater(EntityManager em) {
        this.em = em;
    }

    @Override
    public void buy(String skuCode, Integer count) {
        Inventory inventory = em.find(Inventory.class, skuCode);
        inventory.dec(count);
        InventoryTx tx = new InventoryTx(skuCode, -count);
        em.persist(tx);
    }

    @Override
    public void fill(String skuCode, Integer count) {
        Inventory inventory = em.find(Inventory.class, skuCode);
        inventory.inc(count);
        InventoryTx tx = new InventoryTx(skuCode, count);
        em.persist(tx);
    }

}
