package pl.com.bottega.jpatraining.idgen;

import org.junit.Test;
import pl.com.bottega.jpatraining.BaseJpaTest;

import javax.persistence.EntityExistsException;
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
    public void throwsEntityExistsExceptionWhenPersistingEntityWithId() {
        AuctionWithIdentity auctionWithIdentity = new AuctionWithIdentity();
        auctionWithIdentity.setId(50L);
        template.getStatistics().clear();

        template.executeInTx((em) -> {
            assertThatThrownBy(() -> em.persist(auctionWithIdentity)).isInstanceOf(PersistenceException.class);
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
    public void generatesManyIdsWithSequenceGenerator() {
        template.getStatistics().clear();
        int n = 1000;
        for (int i = 0; i < n; i++) {
            AuctionWithSequence auctionWithSequence = new AuctionWithSequence();
            template.executeInTx((em) -> {
                em.persist(auctionWithSequence);
                System.out.println(auctionWithSequence.getId());
            });
            template.close();
        }

        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(n + n / 100 + 1);
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
