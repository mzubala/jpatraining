package pl.com.bottega.jpatraining.idgen;

import org.junit.Test;
import pl.com.bottega.jpatraining.BaseJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class CompositeIdTest extends BaseJpaTest {

    @Test
    public void insertsEntitiesWithCompositeId() {
        CouponUsage couponUsage = new CouponUsage(new CouponUsageId(1L, 15L));
        template.executeInTx((em) -> {
            em.persist(couponUsage);
        });
        //template.close();

        assertThat(template.getEntityManager().find(CouponUsage.class, new CouponUsageId(1L, 15L))).isNotNull();
    }

}
