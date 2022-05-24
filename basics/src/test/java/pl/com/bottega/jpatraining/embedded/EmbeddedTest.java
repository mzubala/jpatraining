package pl.com.bottega.jpatraining.embedded;

import org.junit.jupiter.api.Test;
import pl.com.bottega.jpatraining.BaseJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

public class EmbeddedTest extends BaseJpaTest {

    @Test
    public void savesSku() {
        // given
        Sku sku = new Sku();
        sku.price = Money.of(10, "USD");
        sku.discountedPrice = Money.of(8, "USD");

        // when
        template.executeInTx((em) -> {
            em.persist(sku);
        });
        template.close();

        // then
        Sku skuFetched = template.getEntityManager().find(Sku.class, sku.id);
        assertThat(skuFetched.discountedPrice).isEqualTo(sku.discountedPrice);
        assertThat(skuFetched.price).isEqualTo(sku.price);
    }

}
