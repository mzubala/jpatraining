package pl.com.bottega.jpatraining.onetomany;

import org.hibernate.collection.spi.PersistentBag;
import org.hibernate.collection.spi.PersistentList;
import org.hibernate.collection.spi.PersistentSet;
import org.hibernate.loader.MultipleBagFetchException;
import org.junit.jupiter.api.Test;
import pl.com.bottega.jpatraining.BaseJpaTest;

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
            var newLike = new Like(fetchedPost);
            fetchedPost.likes.add(newLike);
        });

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(7);
    }

    @Test
    public void insertsNewElementsToAList() {
        // given
        Post post = savedPost();

        // when
        template.executeInTx((em) -> {
            Post fetchedPost = em.find(Post.class, post.id);
            var comment = new Comment(post);
            fetchedPost.comments.add(comment);
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
            .getSingleResult()).isEqualTo(0);
    }

    @Test
    public void multipleBagFetchException() {
        // expect
        template.getEntityManager().createQuery("SELECT p FROM Post p JOIN FETCH p.shares JOIN FETCH p.comments")
            .getResultList();
        assertThatThrownBy(() -> {
            template.getEntityManager().createQuery("SELECT p FROM Post p JOIN FETCH p.shares JOIN FETCH p.likes")
                .getResultList();
        }).hasCauseInstanceOf(MultipleBagFetchException.class);
    }

    @Test
    public void multipleBagFetchExceptionSolution() {
        // given
        int n = 100;
        for(int i = 0; i<n; i++) {
            savedPost();
        }

        // when
        var posts = template.getEntityManager().createQuery("SELECT p FROM Post p JOIN FETCH p.shares", Post.class)
            .getResultList();
        var secondQueryPosts = template.getEntityManager().createQuery("SELECT p FROM Post p JOIN FETCH p.likes")
            .getResultList();

        // then
        posts.forEach((fetchedPost) -> {
            assertThat(fetchedPost.shares).hasSize(1);
            assertThat(fetchedPost.likes).hasSize(2);
        });
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(2);
    }

    @Test
    public void batchSize() {
        // given
        int n = 100;
        for(int i = 0; i<n; i++) {
            savedPost();
        }

        // when
        var posts = template.getEntityManager().createQuery("SELECT p FROM Post p", Post.class)
            .getResultList();

        // then
        posts.forEach((fetchedPost) -> {
            System.out.println("Here");
            assertThat(fetchedPost.shares).hasSize(1);
            assertThat(fetchedPost.likes).hasSize(2);
        });
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
        post.shares.add(new Share());
        return post;
    }

}
