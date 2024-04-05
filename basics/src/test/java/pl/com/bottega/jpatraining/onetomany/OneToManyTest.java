package pl.com.bottega.jpatraining.onetomany;

import org.hibernate.collection.spi.PersistentBag;
import org.hibernate.collection.spi.PersistentList;
import org.hibernate.collection.spi.PersistentSet;
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
            fetchedPost.likes.add(new Like(post));
        });

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(2L);
    }

    @Test
    public void insertsNewElementsToAList() {
        // given
        Post post = savedPost();

        // when
        template.executeInTx((em) -> {
            Post fetchedPost = em.find(Post.class, post.id);
            fetchedPost.comments.add(1, new Comment(post));
        });

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(4L);
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
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(3L);
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
    public void np1SelectTest() {
        // given
        int n = 100;
        for (int i = 0; i < n; i++) {
            savedPost();
        }

        // when
        var posts = template.getEntityManager().createQuery("SELECT p FROM Post p", Post.class).getResultList(); // 1
        for(var p : posts) {
            System.out.println(p.likes.size()); // 1
        } // n

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(n + 1);
    }

    @Test
    public void np1SelectTestSolution() {
        // given
        int n = 100;
        for (int i = 0; i < n; i++) {
            savedPost();
        }

        // when
        var posts = template.getEntityManager().createQuery("SELECT p FROM Post p JOIN FETCH p.likes", Post.class).getResultList(); // 1
        for(var p : posts) {
            System.out.println(p.likes.size()); // 0
        } // 0

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1);
    }

    @Test
    public void np1SelectTestBatchSizeSolution() {
        // given
        int n = 100;
        for (int i = 0; i < n; i++) {
            savedPost();
        }

        // when
        var posts = template.getEntityManager().createQuery("SELECT p FROM Post p", Post.class).getResultList(); // 1
        for(var p : posts) {
            System.out.println(p.comments.size()); // 0
        } // 0

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(6);
    }

    @Test
    public void multipleBagFetchException() {
        // expect
        template.getEntityManager().createQuery("SELECT p FROM Post p JOIN FETCH p.shares JOIN FETCH p.comments JOIN p.tags").getResultList();
        assertThatThrownBy(() -> {
           template.getEntityManager().createQuery("SELECT p FROM Post p JOIN FETCH p.shares JOIN FETCH p.likes").getResultList();
        }).hasCauseInstanceOf(MultipleBagFetchException.class);
    }

    @Test
    public void np1SelectOtherSolution() {
        // given
        int n = 100;
        for (int i = 0; i < n; i++) {
            savedPost();
        }

        // when
        var posts = template.getEntityManager().createQuery("SELECT p FROM Post p LEFT JOIN FETCH p.shares", Post.class).getResultList(); // 1
        template.getEntityManager().createQuery("SELECT p FROM Post p JOIN FETCH p.likes", Post.class).getResultList(); // 1
        for(var p : posts) {
            System.out.println(p.likes.size()); // 0
            System.out.println(p.shares.size()); // 0
        } // 0

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
