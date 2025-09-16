package pl.com.bottega.jpatraining.em;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.FlushModeType;
import org.h2.jdbc.JdbcSQLException;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Test;
import pl.com.bottega.jpatraining.BaseJpaTest;

import jakarta.persistence.EntityExistsException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class EntityManagerTest extends BaseJpaTest {

    @Test
    public void savesAuctionsWithPersist() {
        //given
        Auction auction = newAuction();
        template.getStatistics().clear();

        //when
        template.executeInTx((em) -> {
            em.persist(auction);
            assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(0);
        });

        //then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1);
        assertThat(template.createEntityManager().find(Auction.class, 1L)).isNotNull();
    }


    @Test
    public void savesAuctionsWithMerge() {
        //given
        Auction auction = newAuction();
        template.getStatistics().clear();

        //when
        template.executeInTx((em) -> {
            em.merge(auction);
            assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1);
        });

        //then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(2);
        assertThat(template.createEntityManager().find(Auction.class, 1L)).isNotNull();
    }

    @Test
    public void cantSaveExistingAuctionsWithPersist() {
        //given
        Auction auction = newAuction();
        template.executeInTx(em -> {
            em.persist(auction);
        });
        template.getStatistics().clear();

        //then
        Auction sameAuction = newAuction();
        assertThatThrownBy(() -> {
            template.executeInTx(em -> {
                em.persist(sameAuction);
            });
        }).isInstanceOf(EntityExistsException.class);
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(0);
    }

    @Test
    public void cantSaveExistingAuctionsWithPersistUsingNewEntityManager() {
        //given
        Auction auction = newAuction();
        template.executeInTx(em -> {
            em.persist(auction);
        });
        template.getStatistics().clear();
        template.close();

        //then
        Auction sameAuction = newAuction();
        assertThatThrownBy(() -> {
            template.executeInTx(em -> {
                em.persist(sameAuction);
            });
        }).hasRootCauseInstanceOf(JdbcSQLIntegrityConstraintViolationException.class);
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1);
    }

    @Test
    public void canMergeExistingObjects() {
        //given
        Auction auction = newAuction();
        template.executeInTx(em -> {
            em.persist(auction);
        });
        template.getStatistics().clear();

        //when
        Auction sameAuction = newAuction();
        sameAuction.setName("new name");
        template.executeInTx((em) -> {
            em.merge(sameAuction);
        });

        //then
        assertThat(template.getEntityManager().find(Auction.class, 1L).getName()).isEqualTo("new name");
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1);
    }

    @Test
    public void tracksPersistedEntities() {
        //when
        template.getStatistics().clear();
        template.executeInTx(em -> {
            Auction auction = newAuction();
            em.persist(auction);
            auction.setName("new name");
            auction.setName("new name 2");
            auction.setName("new name 56");
            auction.setName("new name 4632");
            auction.setName("new name 234253");
            auction.setName("new name 2245234");
            auction.setName("new name 3");
        });
        template.close();

        //then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(2);
        assertThat(template.getEntityManager().find(Auction.class, 1L).getName()).isEqualTo("new name 3");
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(3);
    }

    @Test
    public void tracksEntitiesFetchedFromDb() {
        //given
        template.executeInTx(em -> {
            Auction auction = newAuction();
            em.persist(auction);
        });
        template.getStatistics().clear();

        //when
        template.executeInTx(em -> {
            Auction auction = em.find(Auction.class, 1L);
            auction.setName("new name");
            auction.setName("new name 2");
            auction.setName("new name 3");
        });
        template.close();

        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1);
        assertThat(template.getEntityManager().find(Auction.class, 1L).getName()).isEqualTo("new name 3");
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(2);
    }

    @Test
    public void tracksMergedEntities() {
        //given
        template.executeInTx(em -> {
            Auction auction = newAuction();
            em.persist(auction);
        });
        template.close();
        template.getStatistics().clear();

        //when
        template.executeInTx(em -> {
            Auction auction = newAuction();
            auction.setName("new name");
            Auction mergedAuction = em.merge(auction);
            auction.setName("new name 2");
            mergedAuction.setName("new name 42");
            auction.setName("new name 3");
            assertThat(em.contains(mergedAuction)).isTrue();
            assertThat(em.contains(auction)).isFalse();
        });

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(2);
        assertThat(template.getEntityManager().find(Auction.class, 1L).getName()).isEqualTo("new name 42");
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(2);
    }

    @Test
    public void getsReferenceToEntity() {
        //given
        template.executeInTx(em -> {
            Auction auction = newAuction();
            em.persist(auction);
        });
        template.close();
        template.getStatistics().clear();

        // when
        Auction referencedAuction = template.getEntityManager().getReference(Auction.class, 1L);
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(0);
        assertThat(referencedAuction.getId()).isEqualTo(1L);
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(0);
        assertThat(referencedAuction).isInstanceOf(Auction.class).isNotExactlyInstanceOf(Auction.class);
        var name = referencedAuction.getName();
        assertThat(name).isEqualTo("Test");
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1);
    }

    @Test
    public void getsInvalidReference() {
        //given
        template.getStatistics().clear();

        // when
        Auction referencedAuction = template.getEntityManager().getReference(Auction.class, 1L);
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(0);
        assertThat(referencedAuction.getId()).isEqualTo(1L);
        assertThat(referencedAuction).isInstanceOf(Auction.class).isNotExactlyInstanceOf(Auction.class);
        System.out.println(referencedAuction.getClass());
        assertThatThrownBy(referencedAuction::getName).isInstanceOf(EntityNotFoundException.class);
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1);
    }

    @Test
    public void cannotLoadProxyAfterClosingEntityManager() {
        //given
        template.executeInTx(em -> {
            Auction auction = newAuction();
            em.persist(auction);
        });
        template.close();
        template.getStatistics().clear();

        // when
        Auction referencedAuction = template.getEntityManager().getReference(Auction.class, 1L);
        template.close();
        assertThatThrownBy(referencedAuction::getName).isInstanceOf(LazyInitializationException.class);
    }

    @Test
    public void flushesContextOnQuery() {
        // given
        template.getStatistics().clear();

        // expect
        template.executeInTx(em -> {
            Auction auction = newAuction();
            em.persist(auction);
            em.createQuery("SELECT ad FROM CarAd ad").getResultList();
            assertThat(template.getStatistics().getFlushCount()).isEqualTo(0);
            assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1);
            em.createQuery("SELECT a FROM Auction a").getResultList();
            assertThat(template.getStatistics().getFlushCount()).isEqualTo(1);
            assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(3);
        });
    }

    private Auction newAuction() {
        Auction auction = new Auction();
        auction.setId(1L);
        auction.setName("Test");
        return auction;
    }

}
