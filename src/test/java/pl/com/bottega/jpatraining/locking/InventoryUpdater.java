package pl.com.bottega.jpatraining.locking;

public interface InventoryUpdater {


    void buy(String skuCode, Integer count);

    void fill(String skuCode, Integer count);

}
