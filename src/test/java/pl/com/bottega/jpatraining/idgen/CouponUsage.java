package pl.com.bottega.jpatraining.idgen;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Entity
public class CouponUsage {

    @EmbeddedId
    private CouponUsageId id;

    private Instant when;

    public CouponUsage(CouponUsageId couponUsageId) {
        this.id = couponUsageId;
    }

    public CouponUsage() {
    }
}

@Embeddable
class CouponUsageId implements Serializable {
    private Long userId;
    private Long couponId;

    public CouponUsageId(long userId, long couponId) {
        this.userId = userId;
        this.couponId = couponId;
    }

    public CouponUsageId() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CouponUsageId that = (CouponUsageId) o;
        return userId.equals(that.userId) && couponId.equals(that.couponId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, couponId);
    }
}
