package pl.com.bottega.jpatraining.onetomany;

import org.hibernate.collection.spi.PersistentBag;
import org.hibernate.collection.spi.PersistentList;
import org.hibernate.collection.spi.PersistentSet;
import org.hibernate.loader.MultipleBagFetchException;
import org.junit.jupiter.api.Test;
import pl.com.bottega.jpatraining.BaseJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
            fetchedPost.likes.add(new Like(post));
        });

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(2);
    }

    @Test
    public void insertsNewElementsToAList() {
        // given
        Post post = savedPost();

        // when
        template.executeInTx((em) -> {
            Post fetchedPost = em.find(Post.class, post.id);
            fetchedPost.comments.add(new Comment(post));
        });

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(4);
    }

    @Test
    public void insertsNewElementsToASet() {
        // given
        Post post = savedPost();

        // when
        template.executeInTx((em) -> {
            Post fetchedPost = em.find(Post.class, post.id);
            fetchedPost.tags.add(new Tag(post));
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
    public void np1Select() {
        // given
        int n = 100;
        for (int i = 0; i < n; i++) {
            savedPost();
        }

        // when
        List<Post> posts = template.getEntityManager().createQuery("SELECT p FROM Post p", Post.class)
            .getResultList(); // 1
        posts.forEach(post -> {
           System.out.println(post.likes.size()); // 1
        }); // x n

        // then
        assertThat(posts).hasSize(100);
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(n + 1);
    }

    @Test
    public void np1SelectJoinFetchSolution() {
        // given
        int n = 100;
        for (int i = 0; i < n; i++) {
            savedPost();
        }

        // when
        List<Post> posts = template.getEntityManager().createQuery("SELECT p FROM Post p JOIN FETCH p.likes", Post.class)
            .getResultList(); // 1
        posts.forEach(post -> {
            System.out.println(post.likes.size()); // 1
        }); // x n

        // then
        assertThat(posts).hasSize(100);
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1);
    }

    @Test
    public void np1SelectBatchSizeSolution() {
        // given
        int n = 105;
        for (int i = 0; i < n; i++) {
            savedPost();
        }

        // when
        List<Post> posts = template.getEntityManager().createQuery("SELECT p FROM Post p", Post.class)
            .getResultList(); // 1
        posts.forEach(post -> {
            System.out.println(post.comments.size()); // 1
        }); // x n

        // then
        assertThat(posts).hasSize(n);
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(n / Post.COMMENTS_BATCH + 1);
    }

    @Test
    public void cannotFetchMultipleBagsException() {
        // expect
        template.getEntityManager().createQuery("SELECT p FROM Post p JOIN FETCH p.likes JOIN FETCH p.comments JOIN FETCH p.tags")
            .getResultList();
        assertThatThrownBy(() -> {
            template.getEntityManager().createQuery("SELECT p FROM Post p JOIN FETCH p.likes JOIN FETCH p.shares").getResultList();
        }).hasCauseInstanceOf(MultipleBagFetchException.class);
    }

    @Test
    public void splitsQueriesWithJoinFetch() {
        // given
        savedPost();

        // when
        var post = template.getEntityManager().createQuery("SELECT p FROM Post p JOIN FETCH p.likes", Post.class).getResultStream().findFirst().get();
        template.getEntityManager().createQuery("SELECT p FROM Post p JOIN FETCH p.tags", Post.class).getResultList();
        template.getEntityManager().createQuery("SELECT p FROM Post p JOIN FETCH p.shares", Post.class).getResultList();
        template.getEntityManager().createQuery("SELECT p FROM Post p JOIN FETCH p.comments", Post.class).getResultList();

        // then
        assertThat(post.shares).hasSize(1);
        assertThat(post.likes).hasSize(2);
        assertThat(post.tags).hasSize(2);
        assertThat(post.comments).hasSize(2);
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(4);
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
        post.shares.add(new Share());
        return post;
    }

}
