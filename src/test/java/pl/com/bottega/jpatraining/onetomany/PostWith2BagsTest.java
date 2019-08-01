package pl.com.bottega.jpatraining.onetomany;

import org.junit.Test;
import pl.com.bottega.jpatraining.BaseJpaTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PostWith2BagsTest extends BaseJpaTest {

    @Test
    public void cannotFetchTwoBags() {
        assertThatThrownBy(() -> template.executeInTx(em -> {
           em.createQuery("FROM PostWith2Bags p JOIN FETCH p.tags JOIN FETCH p.likes")
           .getResultList();
        })).isInstanceOf(IllegalArgumentException.class);
    }

}
