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
        User userFetched = template.getEntityManager().find(User.class, user.id);
        assertThat(userFetched.name).isEqualTo("Czesiek");
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(0L);
        assertThat(template.getStatistics().getSecondLevelCacheHitCount()).isEqualTo(1L);
    }

    @Test
    public void putsDataIntoCollectionCache() {
        // given
        userWithPhotos();

        // when
        template.executeInTx(em -> {
            User userFetched = em.find(User.class, user.id);
            userFetched.photos.size();
        });

        // then
        assertThat(template.getStatistics().getSecondLevelCachePutCount()).isEqualTo(6L);
        assertThat(template.getStatistics().getSecondLevelCacheMissCount()).isEqualTo(2L);
        assertThat(template.getStatistics().getSecondLevelCacheHitCount()).isEqualTo(0L);
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(2L);

    }

    @Test
    public void readsDataFromCollectionCache() {
        // given
        userWithPhotos();
        template.executeInTx(em -> {
            User userFetched = em.find(User.class, user.id);
            userFetched.photos.size();
        });
        template.getStatistics().clear();
        template.close();

        // when
        template.executeInTx(em -> {
            User userFetched = em.find(User.class, user.id);
            userFetched.photos.size();
        });

        // then
        assertThat(template.getStatistics().getSecondLevelCachePutCount()).isEqualTo(0L);
        assertThat(template.getStatistics().getSecondLevelCacheMissCount()).isEqualTo(0L);
        assertThat(template.getStatistics().getSecondLevelCacheHitCount()).isEqualTo(6L);
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(0L);

    }

    @Test
    public void doesNotReadDataFromCollectionCacheWhenUsingJoinFetch() {
        // given
        userWithPhotos();
        template.executeInTx(em -> {
            User userFetched = em.find(User.class, user.id);
            userFetched.photos.size();
        });
        template.getStatistics().clear();
        template.close();

        // when
        template.getEntityManager().createQuery("FROM User u JOIN FETCH u.photos").getResultList();

        // then
        assertThat(template.getStatistics().getSecondLevelCachePutCount()).isEqualTo(0L);
        assertThat(template.getStatistics().getSecondLevelCacheMissCount()).isEqualTo(0L);
        assertThat(template.getStatistics().getSecondLevelCacheHitCount()).isEqualTo(0L);
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1L);
    }

    @Test
    public void usesDataFromQueryCache() {
        // given
        userWithPhotos();
        template.getEntityManager()
            .createQuery("FROM User u JOIN FETCH u.photos")
            .setHint(QueryHints.HINT_CACHEABLE, true)
            .getResultList();
        template.close();

        // when
        template.getEntityManager()
            .createQuery("FROM User u JOIN FETCH u.photos")
            .setHint(QueryHints.HINT_CACHEABLE, true)
            .getResultList();

        // then
        assertThat(template.getStatistics().getQueryCacheHitCount()).isEqualTo(1L);
        assertThat(template.getStatistics().getQueryCachePutCount()).isEqualTo(1L);
        assertThat(template.getStatistics().getQueryCacheMissCount()).isEqualTo(1L);
        assertThat(template.getStatistics().getSecondLevelCachePutCount()).isEqualTo(6L);
        assertThat(template.getStatistics().getSecondLevelCacheMissCount()).isEqualTo(0L);
        assertThat(template.getStatistics().getSecondLevelCacheHitCount()).isEqualTo(1L);
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1L);
    }

    @Test
    public void clearsDataFromQueryCacheWhenEntitiesAreModified() {
        // given
        userWithPhotos();
        template.getEntityManager()
            .createQuery("FROM User u JOIN FETCH u.photos")
            .setHint(QueryHints.HINT_CACHEABLE, true)
            .getResultList();
        template.close();

        // when
        template.executeInTx(em -> {
            User userFetched = em.find(User.class, user.id);
            userFetched.name = "New name";
        });
        template.getEntityManager()
            .createQuery("FROM User u JOIN FETCH u.photos")
            .setHint(QueryHints.HINT_CACHEABLE, true)
            .getResultList();

        // then
        assertThat(template.getStatistics().getQueryCacheHitCount()).isEqualTo(0L);
        assertThat(template.getStatistics().getQueryCachePutCount()).isEqualTo(2L);
        assertThat(template.getStatistics().getQueryCacheMissCount()).isEqualTo(2L);
        assertThat(template.getStatistics().getSecondLevelCachePutCount()).isEqualTo(7L);
        assertThat(template.getStatistics().getSecondLevelCacheMissCount()).isEqualTo(0L);
        assertThat(template.getStatistics().getSecondLevelCacheHitCount()).isEqualTo(1L);
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(3L);
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
