package pl.com.bottega.jpatraining.em;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.FlushModeType;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.hibernate.LazyInitializationException;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;
import pl.com.bottega.jpatraining.BaseJpaTest;

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
            assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(0L);
        });

        //then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1L);
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
            assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1L);
        });

        //then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(2L);
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
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(0L);
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
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1L);
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
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1L);
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
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(2L);
        assertThat(template.getEntityManager().find(Auction.class, 1L).getName()).isEqualTo("new name 3");
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(3L);
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

        assertThat(template.getEntityManager().find(Auction.class, 1L).getName()).isEqualTo("new name 3");
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(2L);
        //assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(??)
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
            mergedAuction.setName("new name 42");
            auction.setName("new name 2");
        });

        // then
        assertThat(template.getEntityManager().find(Auction.class, 1L).getName()).isEqualTo("new name 42");
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(2L);
        //assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(??)
    }

    @Test
    public void readonlySessionsDontModifyFetchedData() {
        // given
        template.executeInTx((em) -> {
            Auction auction = newAuction();
            em.persist(auction);
        });
        template.close();

        // expect
        template.executeInTx((em) -> {
            em.unwrap(Session.class).setDefaultReadOnly(true);
            em.persist(newAuction(2L));
            var fetchedAuction = em.find(Auction.class, 1L);
            fetchedAuction.setName("Ala");
        });
    }

    @Test
    public void flushesContextInOrder() {
        // given
        template.executeInTx((em) -> {
            em.persist(newAuction(1L));
            em.persist(newAuction(2L));
            em.persist(newAuction(3L));
        });
        template.close();

        // when
        template.executeInTx((em) -> {
            em.setFlushMode(FlushModeType.COMMIT);
            var a = newAuction(3L);
            a.setName("Xyz");
            em.merge(a);
            em.remove(em.find(Auction.class, 1L));
            em.persist(newAuction(1L));
            var a2 = newAuction(2L);
            a2.setName("Xyz");
            em.merge(a2);
            em.persist(newAuction(9L));
        });
    }

    @Test
    public void queryCausesFlush() {
        // given
        template.getStatistics().clear();

        // expect
        template.executeInTx((em) -> {
            em.persist(newAuction());
            em.setFlushMode(FlushModeType.COMMIT);
            em.createNativeQuery("SELECT * FROM \"CarAd\" ad").getResultList();
            assertThat(template.getStatistics().getFlushCount()).isEqualTo(0L);
            assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1L);
            em.createQuery("SELECT a FROM Auction a").getResultList();
            assertThat(template.getStatistics().getFlushCount()).isEqualTo(1L);
            assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(3L);
        });
    }

    @Test
    public void getsLazyEntity() {
        // given
        template.executeInTx((em) -> {
            em.persist(newAuction(1L));
        });
        template.close();
        template.getStatistics().clear();

        // when
        Auction lazyEntity = template.getEntityManager().getReference(Auction.class, 1L);

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(0L);
        assertThat(lazyEntity).isInstanceOf(Auction.class).isNotExactlyInstanceOf(Auction.class);
        System.out.println(lazyEntity.getClass().getName());

        // when
        assertThat(lazyEntity.getId()).isEqualTo(1L);
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(0L);
        assertThat(lazyEntity.getName()).isEqualTo("Test");
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1L);
    }

    @Test
    public void lazyInitializationException() {
        // given
        template.executeInTx((em) -> {
            em.persist(newAuction(1L));
        });
        template.close();
        template.getStatistics().clear();

        // when
        Auction lazyEntity = template.getEntityManager().getReference(Auction.class, 1L);
        template.close();

        // then
        assertThatThrownBy(() -> lazyEntity.getName()).isInstanceOf(LazyInitializationException.class);
    }

    private Auction newAuction() {
        Auction auction = new Auction();
        auction.setId(1L);
        auction.setName("Test");
        return auction;
    }

    private Auction newAuction(Long id) {
        Auction auction = new Auction();
        auction.setId(id);
        auction.setName("Test");
        return auction;
    }

}
