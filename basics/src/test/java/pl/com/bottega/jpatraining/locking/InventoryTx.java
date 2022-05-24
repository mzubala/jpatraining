package pl.com.bottega.jpatraining.locking;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class InventoryTx {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String skuCode;

    @Column(name = "inventory_count")
    private Integer txAmount;

    private InventoryTx() {
    }

    public InventoryTx(String skuCode, Integer count) {
        this.skuCode = skuCode;
        this.txAmount = count;
    }


}
