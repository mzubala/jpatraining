package pl.com.bottega.jpatraining.locking;

import javax.persistence.EntityManager;

public class PesimisticInventoryUpdater implements InventoryUpdater {

    private final EntityManager em;

    public PesimisticInventoryUpdater(EntityManager em) {
        this.em = em;
    }

    @Override
    public void buy(String skuCode, Integer count) {

    }

    @Override
    public void fill(String skuCode, Integer count) {

    }
}
