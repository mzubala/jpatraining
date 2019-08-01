package pl.com.bottega.jpatraining.idgen;

import org.junit.Test;
import pl.com.bottega.jpatraining.BaseJpaTest;

import javax.persistence.EntityManager;
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
            assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(0L);
            assertThat(auctionWithTable.getId()).isNotNull();
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
    public void generatesIdWithSequenceGeneratorForMultipleEntities() {
        template.getStatistics().clear();
        for(int i = 0; i<50; i++) {
            AuctionWithSequence auctionWithSequence = new AuctionWithSequence();
            assertThat(auctionWithSequence.getId()).isNull();

            template.executeInTx((em) -> {
                em.persist(auctionWithSequence);
                assertThat(auctionWithSequence.getId()).isNotNull();
            });
            assertThat(auctionWithSequence.getId()).isEqualTo(i + 1);
        }
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(2L);
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

        //assertThat(auction.getId()).isEqualTo(??);
    }

    private AuctionWithIdentity newAuction() {
        return new AuctionWithIdentity();
    }

}
