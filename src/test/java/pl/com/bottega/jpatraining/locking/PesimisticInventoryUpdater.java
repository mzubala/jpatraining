package pl.com.bottega.jpatraining.locking;

import javax.persistence.EntityManager;

public class PesimisticInventoryUpdater implements InventoryUpdater {

    private final EntityManager em;

    public PesimisticInventoryUpdater(EntityManager em) {
        this.em = em;
    }

    @Override
    public void buy(String skuCode, Integer count) {
        Inventory inventory = em.find(Inventory.class, skuCode);
        InventoryTx inventoryTx = new InventoryTx(skuCode, -count);
        inventory.dec(count);
        em.persist(inventoryTx);
    }

    @Override
    public void fill(String skuCode, Integer count) {
        Inventory inventory = em.find(Inventory.class, skuCode);
        InventoryTx inventoryTx = new InventoryTx(skuCode, count);
        inventory.inc(count);
        em.persist(inventoryTx);
    }
}
