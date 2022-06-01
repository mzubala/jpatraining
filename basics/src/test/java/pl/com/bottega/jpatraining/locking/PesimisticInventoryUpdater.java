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
        em.persist(new InventoryTx(skuCode, -count));
    }

    @Override
    public void fill(String skuCode, Integer count) {
        Inventory inventory = em.find(Inventory.class, skuCode);
        inventory.inc(count);
        em.persist(new InventoryTx(skuCode, +count));
    }
}
