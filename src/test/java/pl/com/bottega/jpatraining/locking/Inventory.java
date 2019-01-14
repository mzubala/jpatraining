package pl.com.bottega.jpatraining.locking;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity
public class Inventory {

    @Id
    private String skuCode;

    @Column(name = "inventory_count")
    private Integer count;

    @Version
    private Long version;

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
