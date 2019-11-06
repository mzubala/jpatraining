package pl.com.bottega.jpatraining.locking;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import static javax.persistence.LockModeType.PESSIMISTIC_WRITE;

public class PesimisticInventoryUpdater implements InventoryUpdater {

    private final EntityManager em;

    public PesimisticInventoryUpdater(EntityManager em) {
        this.em = em;
    }

    @Override
    public void buy(String skuCode, Integer count) {
        Inventory inventory = em.find(Inventory.class, skuCode);
        em.refresh(inventory, PESSIMISTIC_WRITE);
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
