package pl.com.bottega.jpatraining.locking;

import org.junit.Test;
import pl.com.bottega.jpatraining.BaseJpaTest;

import javax.persistence.LockModeType;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;


public class AuctionWithLockingTest extends BaseJpaTest {

    @Test
    public void optimisticLockForceIncrement() {
        // given
        AuctionWithLocking auction = new AuctionWithLocking();
        template.executeInTx(em -> {
            em.persist(auction);
        });
        template.close();

        // when
        template.executeInTx(em -> {
           AuctionWithLocking auctionFetched = em.find(
               AuctionWithLocking.class,
               auction.getId(),
               LockModeType.OPTIMISTIC_FORCE_INCREMENT
           );
           auctionFetched.placeBid(new BigDecimal(100));
        });
        template.close();

        assertThat(
            template.getEntityManager().find(AuctionWithLocking.class, auction.getId()).getVersion()
        ).isEqualTo(auction.getVersion() + 1);
    }

}
