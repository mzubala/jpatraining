package pl.com.bottega.jpatraining.complexid;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

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
