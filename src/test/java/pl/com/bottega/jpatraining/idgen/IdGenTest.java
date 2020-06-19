package pl.com.bottega.jpatraining.idgen;

import org.junit.Test;
import pl.com.bottega.jpatraining.BaseJpaTest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


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
        });
    }

    @Test
    public void failsWhenTryingToPersistAuctionWithIdSet() {
        AuctionWithIdentity auctionWithIdentity = new AuctionWithIdentity();
        auctionWithIdentity.setId(13L);
        template.getStatistics().clear();

        assertThatThrownBy(() -> template.executeInTx((em) -> {
            em.persist(auctionWithIdentity);
        })).isInstanceOf(PersistenceException.class);
    }

    @Test
    public void generatesIdWithIdentityGeneratorWhenMerging() {
        AuctionWithIdentity auctionWithIdentity = new AuctionWithIdentity();
        assertThat(auctionWithIdentity.getId()).isNull();
        template.getStatistics().clear();

        template.executeInTx((em) -> {
            AuctionWithIdentity merged = em.merge(auctionWithIdentity);
            assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1L);
            assertThat(merged.getId()).isNotNull();
            assertThat(auctionWithIdentity.getId()).isNull();
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
    public void generatesIdWithSequenceGeneratorWhenPersistingMultipleEntities() {
        template.getStatistics().clear();
        int N = 20;
        int allocationSize = 5;

        for (int i = 1; i <= N; i++) {
            AuctionWithSequence auctionWithSequence = new AuctionWithSequence();
            template.executeInTx((em) -> {
                em.persist(auctionWithSequence);
                assertThat(auctionWithSequence.getId()).isNotNull();
            });
        }

        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(N + N / allocationSize + 1);
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

    private AuctionWithIdentity newAuction() {
        return new AuctionWithIdentity();
    }

}
