package pl.com.bottega.jpatraining.idgen;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import pl.com.bottega.jpatraining.BaseJpaTest;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;


public class IdGenTest extends BaseJpaTest {

    @Test
    public void generatesIdWithIdentityGenerator() {
        AuctionWithIdentity auctionWithIdentity = new AuctionWithIdentity();
        assertThat(auctionWithIdentity.getId()).isNull();
        template.getStatistics().clear();

        template.executeInTx((em) -> {
            em.persist(auctionWithIdentity);
            assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1L);
            assertThat(auctionWithIdentity.getId()).isNotNull();
            assertThat(auctionWithIdentity.getId()).isEqualTo(1L);
        });
    }

    @Test
    public void generatesIdWithTableGenerator() {
        AuctionWithTable auctionWithTable = new AuctionWithTable();
        assertThat(auctionWithTable.getId()).isNull();
        template.getStatistics().clear();

        template.executeInTx((em) -> {
            em.persist(auctionWithTable);
            //assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(??);
            //assertThat(auctionWithTable.getId())??
        });
    }

    @Test
    public void generatesIdWithSequenceGenerator() {
        AuctionWithSequence auctionWithSequence = new AuctionWithSequence();
        assertThat(auctionWithSequence.getId()).isNull();
        template.getStatistics().clear();

        template.executeInTx((em) -> {
            em.persist(auctionWithSequence);
            assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1L);
            assertThat(auctionWithSequence.getId()).isNotNull();
        });
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(2L);
    }

    @Test
    public void generatesMultipleIdsWithSequence() {
        // given
        template.getStatistics().clear();

        // when
        var n = 200;
        for (int i = 0; i < n; i++) {
            template.executeInTx(em -> {
                em.persist(new AuctionWithSequence());
            });
            template.close();
        }

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(n / 20 + n + 1);
    }

    @Test
    public void generatesIdWithUUID() {
        AuctionWithUUID auctionWithUUID = new AuctionWithUUID();
        assertThat(auctionWithUUID.getId()).isNull();
        template.getStatistics().clear();

        template.executeInTx((em) -> {
            em.persist(auctionWithUUID);
            assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(0L);
            assertThat(auctionWithUUID.getId()).isNotNull();
        });
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1L);
    }

    @Test
    public void mergesAuctionWithGeneratedId() {
        // when
        AuctionWithIdentity mergedAuction = template.executeInTx(em -> {
            AuctionWithIdentity auction = new AuctionWithIdentity();
            auction.setId(40L);
            return em.merge(auction);
        });

        // then
        // assertThat(mergedAuction.getId()).isEqualTo(??);
        // assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(??);
    }

    @Test
    public void orderInCaseOfTransactionRollbackWithTable() {
        // given
        template.executeInTx((em) -> {
            em.persist(new AuctionWithTable());
        });
        template.executeInTx((em) -> {
            em.persist(new AuctionWithTable());
        });
        try {
            template.executeInTx((Consumer<EntityManager>) em -> {
                em.persist(new AuctionWithTable());
                throw new RuntimeException();
            });
        } catch (RuntimeException ex) {

        }
        template.close();

        // when
        AuctionWithTable auction = new AuctionWithTable();
        template.executeInTx(em -> {
            em.persist(auction);
        });

        //assertThat(auction.getId()).isEqualTo(??);
    }

    @Test
    public void orderInCaseOfTransactionRollbackWithIdentity() {
        // given
        template.executeInTx((em) -> {
            em.persist(new AuctionWithIdentity());
        });
        template.executeInTx((em) -> {
            em.persist(new AuctionWithIdentity());
        });
        try {
            template.executeInTx((Consumer<EntityManager>) em -> {
                em.persist(new AuctionWithIdentity());
                throw new RuntimeException();
            });
        } catch (RuntimeException ex) {

        }
        template.close();

        // when
        AuctionWithIdentity auction = new AuctionWithIdentity();
        template.executeInTx(em -> {
            em.persist(auction);
        });

        assertThat(auction.getId()).isEqualTo(4L);
    }

    @Test
    public void orderInCaseOfTransactionRollbackWithSequence() {
        // given
        template.executeInTx((em) -> {
            em.persist(new AuctionWithSequence());
        });
        template.executeInTx((em) -> {
            em.persist(new AuctionWithSequence());
        });
        try {
            template.executeInTx((Consumer<EntityManager>) em -> {
                em.persist(new AuctionWithSequence());
                throw new RuntimeException();
            });
        } catch (RuntimeException ex) {

        }
        template.close();

        // when
        AuctionWithSequence auction = new AuctionWithSequence();
        template.executeInTx(em -> {
            em.persist(auction);
        });

        assertThat(auction.getId()).isEqualTo(4L);
    }

    private AuctionWithIdentity newAuction() {
        return new AuctionWithIdentity();
    }

}
