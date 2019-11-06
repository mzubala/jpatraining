package pl.com.bottega.jpatraining.locking;

import org.junit.Test;
import pl.com.bottega.jpatraining.BaseJpaTest;

import javax.persistence.LockModeType;

import static javax.persistence.LockModeType.OPTIMISTIC_FORCE_INCREMENT;
import static org.assertj.core.api.Assertions.assertThat;

public class AuctionLockingTest extends BaseJpaTest {

    @Test
    public void updatesAuctionVersion() {
        // given
        Auction auction = new Auction();
        template.executeInTx(em -> {
            em.persist(auction);
        });
        template.close();

        // when
        template.executeInTx(em -> {
            Auction auctionFetched = em.find(Auction.class, auction.getId(), OPTIMISTIC_FORCE_INCREMENT);
            auctionFetched.placeBid();
        });
        template.close();

        // then
        Auction auctionFetched = template.getEntityManager().find(Auction.class, auction.getId());
        assertThat(auctionFetched.getVersion()).isEqualTo(1L);
    }

}
