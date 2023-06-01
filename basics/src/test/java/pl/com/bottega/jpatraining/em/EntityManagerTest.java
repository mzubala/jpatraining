package pl.com.bottega.jpatraining.em;

import org.h2.jdbc.JdbcSQLException;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
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
            //assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(??)
        });

        //then
        //assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(??);
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
            //assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(??)
        });

        //then
        //assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(??);
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
        //assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(??);
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
        //assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(??);
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
        //assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(??);
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
        //assertThat(template.getEntityManager().find(Auction.class, 1L).getName()).isEqualTo(???)
        //assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(??)
        //assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(??)
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

        //assertThat(template.getEntityManager().find(Auction.class, 1L).getName()).isEqualTo(???)
        //assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(??)
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
        });

        // then
        //assertThat(template.getEntityManager().find(Auction.class, 1L).getName()).isEqualTo(???)
        //assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(??)
        //assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(??)
    }

    private Auction newAuction() {
        Auction auction = new Auction();
        auction.setId(1L);
        auction.setName("Test");
        return auction;
    }

}
