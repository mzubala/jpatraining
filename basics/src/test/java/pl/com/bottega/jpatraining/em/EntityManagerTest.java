package pl.com.bottega.jpatraining.em;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.LockModeType;
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
            auction.setName("new name 43");
        });

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(2);
        assertThat(template.getEntityManager().find(Auction.class, 1L).getName()).isEqualTo("new name 42");
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(2);
    }

    @Test
    public void queryCausesFlush() {
        // given
        template.getStatistics().clear();

        // expect
        template.executeInTx(em -> {
           em.persist(newAuction());
           assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(0);
           em.createQuery("SELECT a FROM Auction a WHERE a.name = 'Test'").getResultList();
           assertThat(template.getStatistics().getFlushCount()).isEqualTo(1);
           assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(2);
        });
    }

    @Test
    public void queryToNotPersistentEntityDoesNotCauseFlush() {
        // given
        template.getStatistics().clear();

        // expect
        template.executeInTx(em -> {
            em.persist(newAuction());
            assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(0);
            em.createQuery("SELECT a FROM CarAd a").getResultList();
            assertThat(template.getStatistics().getFlushCount()).isEqualTo(0);
            assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1);
        });
    }

    @Test
    public void nativeQueryAlwaysCausesFlush() {
        // given
        template.getStatistics().clear();

        // expect
        template.executeInTx(em -> {
            em.persist(newAuction());
            assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(0);
            em.createNativeQuery("SELECT * FROM \"CarAd\"").getResultList();
            assertThat(template.getStatistics().getFlushCount()).isEqualTo(1);
            assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(2);
        });
    }

    @Test
    public void getsLazyEntity() {
        //given
        Auction auction = newAuction();
        template.executeInTx(em -> {
            em.persist(auction);
        });
        template.close();
        template.getStatistics().clear();

        //expect
        Auction reference = template.getEntityManager().getReference(Auction.class, 1L);
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(0);
        assertThat(reference).isNotExactlyInstanceOf(Auction.class).isInstanceOf(Auction.class);
        System.out.println(reference.getClass());
        assertThat(reference.getId()).isEqualTo(1L);
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(0);
        assertThat(reference.getName()).isEqualTo("Test");
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1);
        assertThat(reference.getName()).isEqualTo("Test");
        assertThat(reference.getName()).isEqualTo("Test");
        assertThat(reference.getName()).isEqualTo("Test");
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1);
    }

    @Test
    public void throwsLazyInitException() {
        //given
        Auction auction = newAuction();
        template.executeInTx(em -> {
            em.persist(auction);
        });
        template.close();
        template.getStatistics().clear();

        //expect
        Auction reference = template.getEntityManager().getReference(Auction.class, 1L);
        template.close();
        assertThatThrownBy(reference::getName).isInstanceOf(LazyInitializationException.class);
    }

    @Test
    public void referenceWithInvalidId() {
        //expect
        Auction reference = template.getEntityManager().getReference(Auction.class, 7888L);
        assertThat(reference.getId()).isEqualTo(7888L);
        assertThatThrownBy(reference::getName).isInstanceOf(EntityNotFoundException.class);
    }

    private Auction newAuction() {
        Auction auction = new Auction();
        auction.setId(1L);
        auction.setName("Test");
        return auction;
    }

}
