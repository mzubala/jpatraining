package pl.com.bottega.jpatraining.complexid;

import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class CouponUsageId implements Serializable {
    private Long userId;
    private Long couponId;

    public CouponUsageId(Long userId, Long couponId) {
        this.userId = userId;
        this.couponId = couponId;
    }

    CouponUsageId() {
    }

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
