package pl.com.bottega.jpatraining.idgen;

import org.junit.jupiter.api.Test;
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
            assertThat(auctionWithIdentity.getId()).isNotNull();
            assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1L);
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
            assertThat(auctionWithSequence.getId()).isNotNull();
            assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(2L);
        });
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(3L);
    }

    @Test
    public void generatesIdWithSequenceGeneratorInBatches() {
        // given
        template.getStatistics().clear();
        int n = 1000;

        // when
        for (int i = 0; i < n; i++) {
            template.executeInTx((em) -> {
                AuctionWithSequence auctionWithSequence = new AuctionWithSequence();
                em.persist(auctionWithSequence);
            });
        }

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(n + n / 50 + 1);
    }

    @Test
    public void orderInCaseOfTransactionRollbackWithTable() {
        // given
        template.executeInTx((em) -> {
            em.persist(new AuctionWithSequence()); // 1
        });
        template.executeInTx((em) -> {
            em.persist(new AuctionWithSequence()); // 2
        });
        try {
            template.executeInTx((Consumer<EntityManager>) em -> {
                em.persist(new AuctionWithSequence()); // 3
                throw new RuntimeException();
            });
        } catch (RuntimeException ex) {

        }
        template.close();

        // when
        AuctionWithSequence auction = new AuctionWithSequence();
        template.executeInTx(em -> {
            em.persist(auction); // 3? 4?
        });

        assertThat(auction.getId()).isEqualTo(4);
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

        assertThat(auction.getId()).isEqualTo(4);
    }

    private AuctionWithIdentity newAuction() {
        return new AuctionWithIdentity();
    }

}
