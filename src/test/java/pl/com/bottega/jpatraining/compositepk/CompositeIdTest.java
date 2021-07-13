package pl.com.bottega.jpatraining.compositepk;

import org.junit.Test;
import pl.com.bottega.jpatraining.BaseJpaTest;

import java.time.Instant;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class CompositeIdTest extends BaseJpaTest {

    @Test
    public void savesAndReadsEntitiesWithCompositePk() {
        CouponUsageId id = new CouponUsageId(1L, 50L);
        CouponUsage couponUsage = new CouponUsage(id, Instant.now());

        template.executeInTx((em) -> {
            em.persist(couponUsage);
        });
        template.close();

        assertThat(template.getEntityManager().find(CouponUsage.class, id)).isNotNull();
    }

}
