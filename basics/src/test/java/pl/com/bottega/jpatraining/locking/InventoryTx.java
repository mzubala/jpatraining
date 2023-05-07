package pl.com.bottega.jpatraining.locking;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

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
