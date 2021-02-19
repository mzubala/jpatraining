package pl.com.bottega.jpatraining.locking;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

public class PesimisticInventoryUpdater implements InventoryUpdater {

    private final EntityManager entityManager;

    public PesimisticInventoryUpdater(EntityManager em) {
        this.entityManager = em;
    }

    @Override
    public void buy(String skuCode, Integer count) {
        entityManager.detach(entityManager.getReference(Inventory.class, skuCode));
        Inventory inventory = entityManager.find(Inventory.class, skuCode, LockModeType.PESSIMISTIC_WRITE);
        //entityManager.refresh(inventory, LockModeType.PESSIMISTIC_WRITE);

        inventory.dec(count);
        InventoryTx inventoryTx = new InventoryTx(skuCode, -count);
        entityManager.persist(inventoryTx);
    }

    @Override
    public void fill(String skuCode, Integer count) {
        Inventory inventory = entityManager.find(Inventory.class, skuCode);
        inventory.inc(count);
        InventoryTx inventoryTx = new InventoryTx(skuCode, count);
        entityManager.persist(inventoryTx);
    }
}
