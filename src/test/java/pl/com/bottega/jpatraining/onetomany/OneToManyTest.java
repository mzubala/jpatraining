package pl.com.bottega.jpatraining.onetomany;

import org.hibernate.LazyInitializationException;
import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.collection.internal.PersistentList;
import org.hibernate.collection.internal.PersistentSet;
import org.junit.Test;
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
            assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1L);
            fetchedPost.likes.add(new Like(fetchedPost));
            assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1L);
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
            assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1L);
            fetchedPost.comments.add(new Comment(fetchedPost));
            assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1L);
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
            assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1L);
            fetchedPost.tags.add(new Tag(fetchedPost));
            assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(2L);
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
    public void throwsLazyLoadingException() {
        // given
        Post post = savedPost();

        // when
        Post fetchedPost = template.executeInTx((em) -> {
            return em.find(Post.class, post.id);
        });
        template.close();

        // then
        assertThatThrownBy(() -> System.out.println(fetchedPost.comments.get(0).id))
            .isInstanceOf(LazyInitializationException.class);
    }

    @Test
    public void nP1SelectProblem() {
        // given
        int n = 100;
        for(int i = 0; i<n; i++) {
            savedPost();
        }
        template.close();
        template.getStatistics().clear();

        // when
        template.executeInTx((em) -> {
            List<Post> posts = em.createQuery("SELECT p FROM Post p", Post.class).getResultList();
            assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1L);
            posts.forEach(p -> {
                System.out.println(p.comments.get(0).id);
            });
        });

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(n + 1L);
    }

    @Test
    public void nP1SelectProblemSolution() {
        // given
        int n = 100;
        for(int i = 0; i<n; i++) {
            savedPost();
        }
        template.close();
        template.getStatistics().clear();

        // when
        template.executeInTx((em) -> {
            List<Post> posts = em.createQuery("SELECT DISTINCT p FROM Post p JOIN FETCH p.comments", Post.class).getResultList();
            assertThat(posts.size()).isEqualTo(n);
            assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1L);
            posts.forEach(p -> {
                System.out.println(p.comments.get(0).id);
            });
        });

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1L);
    }

    private Post savedPost() {
        Post post = newPost();
        template.executeInTx((em) -> {
            em.persist(post);
        });
        template.close();
        template.getStatistics().clear();
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
