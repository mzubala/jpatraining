package pl.com.bottega.jpatraining.compositepk;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.io.Serializable;
import java.time.Instant;

@Entity
public class CouponUsage {

    @EmbeddedId
    private CouponUsageId id;
    private Instant when;

    public CouponUsage(CouponUsageId id, Instant usedAt) {
        this.id = id;
        this.when = usedAt;
    }

    private CouponUsage() {}
}

@Embeddable
class CouponUsageId implements Serializable {
    private Long userId;
    private Long couponId;

    public CouponUsageId(long userId, long couponId) {
        this.userId = userId;
        this.couponId = couponId;
    }

    private CouponUsageId() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CouponUsageId that = (CouponUsageId) o;

        if (!userId.equals(that.userId)) return false;
        return couponId.equals(that.couponId);
    }

    @Override
    public int hashCode() {
        int result = userId.hashCode();
        result = 31 * result + couponId.hashCode();
        return result;
    }
}
