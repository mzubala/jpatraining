package pl.com.bottega.jpatraining.onetomany;

import jakarta.persistence.QueryHint;
import org.hibernate.collection.spi.PersistentBag;
import org.hibernate.collection.spi.PersistentList;
import org.hibernate.collection.spi.PersistentSet;
import org.hibernate.jpa.AvailableHints;
import org.hibernate.jpa.QueryHints;
import org.hibernate.loader.MultipleBagFetchException;
import org.junit.jupiter.api.Test;
import pl.com.bottega.jpatraining.BaseJpaTest;

import static org.assertj.core.api.Assertions.*;

public class OneToManyTest extends BaseJpaTest {

    @Test
    public void hasDifferentCollectionTypes() {
        // given
        Post post = savedPost();

        // when
        Post fetchedPost = template.getEntityManager().find(Post.class, post.id);

        // then
        assertThat(fetchedPost.comments).isExactlyInstanceOf(PersistentList.class);
        assertThat(fetchedPost.tags).isExactlyInstanceOf(PersistentSet.class);
        assertThat(fetchedPost.likes).isExactlyInstanceOf(PersistentBag.class);
    }


    @Test
    public void insertsNewElementsToABag() {
        // given
        Post post = savedPost();

        // when
        template.executeInTx((em) -> {
            Post fetchedPost = em.find(Post.class, post.id);
            fetchedPost.likes.add(new Like(fetchedPost));
        });

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(3);
    }

    @Test
    public void insertsNewElementsToAList() {
        // given
        Post post = savedPost();

        // when
        template.executeInTx((em) -> {
            Post fetchedPost = em.find(Post.class, post.id);
            fetchedPost.comments.add(new Comment(fetchedPost));
        });

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(5);
    }

    @Test
    public void insertsNewElementsToASet() {
        // given
        Post post = savedPost();

        // when
        template.executeInTx((em) -> {
            Post fetchedPost = em.find(Post.class, post.id);
            fetchedPost.tags.add(new Tag(fetchedPost));
        });

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(3);
    }

    @Test
    public void removesOrphans() {
        // given
        Post post = savedPost();

        // when
        template.executeInTx((em) -> {
            Post fetchedPost = em.find(Post.class, post.id);
            fetchedPost.tags.clear();
        });
        template.close();

        // then
        assertThat(template.getEntityManager().createQuery("SELECT count(t) FROM Tag t")
            .getSingleResult()).isEqualTo(0L);
    }

    @Test
    public void np1SelectProblem() {
        // given
        int n = 100;
        for (int i = 0; i < n; i++) {
            savedPost();
        }
        template.close();
        template.getStatistics().clear();

        // when
        var posts = template.getEntityManager().createQuery("SELECT p FROM Post p", Post.class)
            .getResultList(); // 1 query
        posts.forEach(p -> { // * n
            p.comments.forEach(System.out::println); // 1 query
        });

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(n + 1);
    }

    @Test
    public void np1SelectProblemJoinFetchSolution() {
        // given
        int n = 100;
        for (int i = 0; i < n; i++) {
            savedPost();
        }
        template.close();
        template.getStatistics().clear();

        // when
        var posts = template.getEntityManager().createQuery("SELECT p FROM Post p JOIN FETCH p.comments", Post.class)
            .getResultList(); // 1 query
        posts.forEach(p -> { // * n
            p.comments.forEach(System.out::println); // 0 query
        });

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1);
    }

    @Test
    public void np1SelectProblemBatchSizeSolution() {
        // given
        int n = 100;
        int batchSize = 20;
        for (int i = 0; i < n; i++) {
            savedPost();
        }
        template.close();
        template.getStatistics().clear();

        // when
        var posts = template.getEntityManager().createQuery("SELECT p FROM Post p", Post.class)
            .getResultList(); // 1 query
        posts.forEach(p -> { // * n
            p.likes.forEach(System.out::println); // 0 query
        });

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(n / batchSize + 1);
    }

    @Test
    public void cannotFetchTwoBagsInOneQuery() {
        // expect
        template.getEntityManager().createQuery("SELECT p FROM Post p JOIN FETCH p.shares JOIN FETCH p.comments", Post.class)
            .getResultList();

        assertThatThrownBy(() -> {
            template.getEntityManager().createQuery("SELECT p FROM Post p JOIN FETCH p.shares JOIN FETCH p.likes", Post.class)
                .getResultList();
        }).hasCauseInstanceOf(MultipleBagFetchException.class);
        assertThatThrownBy(() -> {
            template.getEntityManager().createQuery("SELECT p FROM Post p JOIN FETCH p.shares JOIN FETCH p.likes JOIN FETCH p.comments", Post.class)
                .getResultList();
        }).hasCauseInstanceOf(MultipleBagFetchException.class);
    }

    @Test
    public void joinFetchInSeparateQueries() {
        // given
        int n = 100;
        for (int i = 0; i < n; i++) {
            savedPost();
        }
        template.close();
        template.getStatistics().clear();

        // when
        var posts = template.getEntityManager().createQuery("SELECT p FROM Post p LEFT JOIN FETCH p.comments", Post.class)
            .getResultList(); // 1 query
        var post2 = template.getEntityManager().createQuery("SELECT p FROM Post p LEFT JOIN FETCH p.shares", Post.class)
            .getResultList(); // 1 query
        template.getEntityManager().createQuery("SELECT p FROM Post p LEFT JOIN FETCH p.likes", Post.class)
            .getResultList(); // 1 query
        template.getEntityManager().createQuery("SELECT p FROM Post p LEFT JOIN FETCH p.tags", Post.class)
            .getResultList(); // 1 query
        posts.forEach(p -> {
            p.comments.forEach(System.out::println); // 0 query
            p.shares.forEach(System.out::println); // 0 query
            p.likes.forEach(System.out::println); // 0 query
            p.tags.forEach(System.out::println); // 0 query
        });

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(4);
    }

    @Test
    public void np1SelectEntityGraphSolution() {
        // given
        int n = 100;
        for (int i = 0; i < n; i++) {
            savedPost();
        }
        template.close();
        template.getStatistics().clear();

        // when
        var graph = template.getEntityManager().getEntityGraph("post-with-everything");
        var posts = template.getEntityManager().createQuery("SELECT p FROM Post p", Post.class)
            .setHint(AvailableHints.HINT_SPEC_LOAD_GRAPH, graph)
            .getResultList(); // 1 query
        template.getEntityManager().createQuery("SELECT p FROM Post p LEFT JOIN FETCH p.shares", Post.class)
            .getResultList(); // 1 query
        posts.forEach(p -> {
            p.comments.forEach(System.out::println); // 0 query
            p.shares.forEach(System.out::println); // 0 query
            p.likes.forEach(System.out::println); // 0 query
            p.tags.forEach(System.out::println); // 0 query
        });

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(2);
    }


    private Post savedPost() {
        Post post = newPost();
        template.executeInTx((em) -> {
            em.persist(post);
        });
        template.getStatistics().clear();
        template.close();
        return post;
    }

    private Post newPost() {
        Post post = new Post();
        post.comments.add(new Comment(post));
        post.comments.add(new Comment(post));
        post.likes.add(new Like(post));
        post.likes.add(new Like(post));
        post.tags.add(new Tag(post));
        post.tags.add(new Tag(post));
        return post;
    }

}
