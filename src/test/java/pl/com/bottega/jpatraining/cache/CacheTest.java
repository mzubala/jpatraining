package pl.com.bottega.jpatraining.cache;

import org.hibernate.SessionFactory;
import org.hibernate.jpa.QueryHints;
import org.hibernate.stat.CacheRegionStatistics;
import org.hibernate.stat.SecondLevelCacheStatistics;
import org.hibernate.stat.Statistics;
import org.junit.Test;
import pl.com.bottega.jpatraining.BaseJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

public class CacheTest extends BaseJpaTest {

    public CacheTest() {
        template.setUnitName("jpatraining-l2");
    }

    @Test
    public void readsEntitiesFromL2Cache() {
        // given
        userWithPhotos();

        // when
        template.getEntityManager().find(User.class, user.id);
        template.close();
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(0);
        assertThat(template.getStatistics().getSecondLevelCacheMissCount()).isEqualTo(0);
        assertThat(template.getStatistics().getSecondLevelCacheHitCount()).isEqualTo(1);
        assertThat(template.getStatistics().getSecondLevelCachePutCount()).isEqualTo(0);

        // then
        template.getEntityManager().find(User.class, user.id);
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(0);
        assertThat(template.getStatistics().getSecondLevelCacheHitCount()).isEqualTo(2);
    }

    @Test
    public void updatesL2Cache() {
        // given
        userWithPhotos();
        template.getEntityManager().find(User.class, user.id);
        template.close();

        // when
        template.executeInTx((em) -> {
            User u = em.find(User.class, user.id);
            u.name = "Czesiek";
        });
        assertThat(template.getStatistics().getSecondLevelCachePutCount()).isEqualTo(1);
        template.close();
        template.getStatistics().clear();


        // then
        template.getEntityManager().find(User.class, user.id);
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(0);
        assertThat(template.getStatistics().getSecondLevelCacheHitCount()).isEqualTo(1);
    }

    @Test
    public void readsCollectionElementsFromCache() {
        // given
        userWithPhotos();

        // when
        template.executeInTx(em -> {
            User userFetched = em.find(User.class, user.id);
            userFetched.photos.size();
        });
        template.close();
        assertThat(template.getStatistics().getSecondLevelCacheMissCount()).isEqualTo(1);
        assertThat(template.getStatistics().getSecondLevelCacheHitCount()).isEqualTo(1);
        assertThat(template.getStatistics().getSecondLevelCachePutCount()).isEqualTo(5);

        // then
        User userFetched = template.getEntityManager().find(User.class, user.id);
        userFetched.photos.size();
        assertThat(template.getStatistics().getSecondLevelCacheMissCount()).isEqualTo(1);
        assertThat(template.getStatistics().getSecondLevelCacheHitCount()).isEqualTo(7);
        assertThat(template.getStatistics().getSecondLevelCachePutCount()).isEqualTo(5);
    }

    @Test
    public void cachesQueries() {
        // given
        userWithPhotos();

        // when
        template.getEntityManager()
            .createQuery("FROM Photo p")
            .setHint(QueryHints.HINT_CACHEABLE, true)
            .getResultList();
        template.close();
        assertThat(template.getStatistics().getSecondLevelCachePutCount()).isEqualTo(4);

        template.executeInTx((em) -> {
            Photo photo = new Photo("ela");
            em.persist(photo);
        });
        template.close();

        // then
        template.getStatistics().clear();
        template.getEntityManager()
            .createQuery("FROM Photo p")
            .setHint(QueryHints.HINT_CACHEABLE, true)
            .getResultList();
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1);
    }

    private User user;

    private void userWithPhotos() {
        user = new User("Janek");
        user.addPhoto("f1");
        user.addPhoto("f2");
        user.addPhoto("f3");
        user.addPhoto("f4");
        template.executeInTx((em) -> {
            em.persist(user);
        });
        template.close();
        template.getStatistics().clear();
    }

}
