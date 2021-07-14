package pl.com.bottega.jpatraining.cache;

import org.hibernate.jpa.QueryHints;
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
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1);
        assertThat(template.getStatistics().getSecondLevelCacheMissCount()).isEqualTo(1);
        assertThat(template.getStatistics().getSecondLevelCacheHitCount()).isEqualTo(0);
        assertThat(template.getStatistics().getSecondLevelCachePutCount()).isEqualTo(1);

        // then
        template.getEntityManager().find(User.class, user.id);
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1);
        assertThat(template.getStatistics().getSecondLevelCacheHitCount()).isEqualTo(1);
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
        template.close();
        template.getStatistics().clear();

        // then
        template.getEntityManager().find(User.class, user.id);
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(0L);
        assertThat(template.getStatistics().getSecondLevelCacheHitCount()).isEqualTo(1L);
    }

    @Test
    public void readsCollectionElementsFromCache() {
        // given
        userWithPhotos();

        // when
        template.executeInTx(em -> {
            User userFetched = em.find(User.class, user.id); // 1 miss + 1 put
            userFetched.photos.size(); // 1 miss + 1 put collection + 4 put photo
        });
        template.close();
        assertThat(template.getStatistics().getSecondLevelCacheMissCount()).isEqualTo(2);
        assertThat(template.getStatistics().getSecondLevelCacheHitCount()).isEqualTo(0);
        assertThat(template.getStatistics().getSecondLevelCachePutCount()).isEqualTo(6);

        // then
        User userFetched = template.getEntityManager().find(User.class, user.id);
        userFetched.photos.size();
        assertThat(template.getStatistics().getSecondLevelCacheMissCount()).isEqualTo(2);
        assertThat(template.getStatistics().getSecondLevelCacheHitCount()).isEqualTo(6);
        assertThat(template.getStatistics().getSecondLevelCachePutCount()).isEqualTo(6);
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
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1);
        assertThat(template.getStatistics().getSecondLevelCachePutCount()).isEqualTo(4);
        assertThat(template.getStatistics().getQueryCachePutCount()).isEqualTo(1);
        assertThat(template.getStatistics().getQueryCacheMissCount()).isEqualTo(1);

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
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(0);
        assertThat(template.getStatistics().getQueryCachePutCount()).isEqualTo(0);
        assertThat(template.getStatistics().getQueryCacheMissCount()).isEqualTo(0);
        assertThat(template.getStatistics().getQueryCacheHitCount()).isEqualTo(1);
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
