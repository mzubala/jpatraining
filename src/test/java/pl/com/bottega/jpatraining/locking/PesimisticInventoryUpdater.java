package pl.com.bottega.jpatraining.locking;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

public class PesimisticInventoryUpdater implements InventoryUpdater {

    private final EntityManager em;

    public PesimisticInventoryUpdater(EntityManager em) {
        this.em = em;
    }

    @Override
    public void buy(String skuCode, Integer count) {
        em.clear();
        Inventory inventory = em.find(Inventory.class, skuCode, LockModeType.PESSIMISTIC_WRITE);
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
