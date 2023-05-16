package pl.com.bottega.jpatraining.locking;

import jakarta.persistence.EntityManager;

public class OptimisticInventoryUpdater implements InventoryUpdater {

    private final EntityManager entityManager;

    public OptimisticInventoryUpdater(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void buy(String skuCode, Integer count) {
        var inventory = entityManager.find(Inventory.class, skuCode);
        inventory.dec(count);
        entityManager.persist(new InventoryTx(skuCode, -count));
    }

    @Override
    public void fill(String skuCode, Integer count) {
        var inventory = entityManager.find(Inventory.class, skuCode);
        inventory.inc(count);
        entityManager.persist(new InventoryTx(skuCode, count));
    }

}
