package pl.com.bottega.jpatraining.complexid;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

import java.time.Instant;

@Entity
public class CouponUsage {
    @EmbeddedId
    private CouponUsageId id;

    private final Instant usedAt;

    public CouponUsage(CouponUsageId id) {
        this.id = id;
        this.usedAt = Instant.now();
    }

    private CouponUsage() {
        usedAt = null;
    }
}
