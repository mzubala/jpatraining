package pl.com.bottega.jpatraining.locking;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Inventory {

    @Id
    private String skuCode;

    @Column(name = "inventory_count")
    private Integer count;

    private Inventory() {
    }

    public Inventory(String skuCode, Integer count) {
        this.skuCode = skuCode;
        this.count = count;
    }

    public void inc(Integer count) {
        this.count += count;
    }

    public void dec(Integer count) {
        if(this.count < count) {
            throw new NotEnoughInventoryException();
        }
        this.count -= count;
    }

    public Integer getCount() {
        return count;
    }
}
