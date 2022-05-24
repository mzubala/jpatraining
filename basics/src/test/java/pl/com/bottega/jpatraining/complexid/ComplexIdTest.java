package pl.com.bottega.jpatraining.complexid;

import org.junit.jupiter.api.Test;
import pl.com.bottega.jpatraining.BaseJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

public class ComplexIdTest extends BaseJpaTest {

    @Test
    public void usesComplexId() {
        CouponUsageId id = new CouponUsageId(1L, 1L);
        template.executeInTx(em -> {
            em.persist(new CouponUsage(id));
        });
        template.close();

        assertThat(template.getEntityManager().find(CouponUsage.class, id)).isNotNull();
    }
}
