package pl.com.bottega.jpatraining.complexid;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

@Entity
public class CouponUsage {
    @EmbeddedId
    private CouponUsageId id;

    public CouponUsage(CouponUsageId id) {
        this.id = id;
    }

    public CouponUsage() {
    }
}
