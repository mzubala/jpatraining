package pl.com.bottega.jpatraining.em;

import jakarta.persistence.EntityNotFoundException;
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
            assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(0L);
        });

        //then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1L);
        assertThat(template.createEntityManager().find(Auction.class, 1L)).isNotNull();
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(2L);
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
            assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(0L);
        });
        template.close();

        //then
        assertThat(template.getEntityManager().find(Auction.class, 1L).getName()).isEqualTo("new name 3");
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(3L);
        //assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(??)
    }

    @Test
    public void tracksEntitiesFetchedFromDb() {
        //given
        template.executeInTx(em -> {
            Auction auction = newAuction();
            em.persist(auction);
        });
        template.close();
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
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(3L);
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
            auction.setName("new name 2");
            mergedAuction.setName("new name 42");
            auction.setName("new name 43");
        });

        // then
        assertThat(template.getEntityManager().find(Auction.class, 1L).getName()).isEqualTo("new name 42");
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(2L);
        //assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(??)
    }

    @Test
    public void getsLazyNonExistingEntity() {
        // given
        template.getStatistics().clear();

        // when
        var reference = template.getEntityManager().getReference(Auction.class, 1L);

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(0L);
        assertThat(reference).isNotNull();
        assertThat(reference).isInstanceOf(Auction.class).isNotExactlyInstanceOf(Auction.class);
        System.out.println(reference.getClass());

        // when
        assertThatThrownBy(() -> {
           reference.getName();
        }).isInstanceOf(EntityNotFoundException.class);
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1L);
    }

    @Test
    public void getsLazyExistingEntity() {
        // given
        template.executeInTx(em -> {
            Auction auction = newAuction();
            em.persist(auction);
        });
        template.close();
        template.getStatistics().clear();

        // when
        var reference = template.getEntityManager().getReference(Auction.class, 1L);

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(0L);
        assertThat(reference).isNotNull();
        assertThat(reference).isInstanceOf(Auction.class).isNotExactlyInstanceOf(Auction.class);
        System.out.println(reference.getClass());

        // when
        assertThat(reference.getName()).isEqualTo("Test"); // Lazy Loading
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1L);
    }

    @Test
    public void lazyLoadingFailsWhenEntityManagerIsClosed() {
        // given
        template.executeInTx(em -> {
            Auction auction = newAuction();
            em.persist(auction);
        });
        template.close();
        template.getStatistics().clear();

        // when
        var reference = template.getEntityManager().getReference(Auction.class, 1L);
        template.close();

        // then
        assertThatThrownBy(() -> reference.getName()).isInstanceOf(LazyInitializationException.class);
    }

    private Auction newAuction() {
        Auction auction = new Auction();
        auction.setId(1L);
        auction.setName("Test");
        return auction;
    }

}
