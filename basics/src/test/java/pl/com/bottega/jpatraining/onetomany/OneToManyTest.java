package pl.com.bottega.jpatraining.onetomany;

import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.collection.internal.PersistentList;
import org.hibernate.collection.internal.PersistentSet;
import org.hibernate.jpa.QueryHints;
import org.junit.jupiter.api.Test;
import pl.com.bottega.jpatraining.BaseJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(2L);
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
        assertThat(template.getEntityManager().createQuery("SELECT count(t) FROM Tag t").getSingleResult()).isEqualTo(
            0L);
    }

    @Test
    public void np1Select() {
        // given
        int n = 100;
        for (int i = 0; i < n; i++) {
            savedPost();
        }

        // when
        var posts = template.getEntityManager().createQuery("SELECT p FROM Post p", Post.class).getResultList(); // 1

        // then
        posts.forEach(post -> {
            post.tags.forEach(tag -> System.out.println(tag.id)); // 1
        }); // x n
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(n + 1);
    }

    @Test
    public void np1SelectSolutionWithJoinFetch() {
        // given
        int n = 100;
        for (int i = 0; i < n; i++) {
            savedPost();
        }

        // when
        var posts = template.getEntityManager().createQuery("SELECT DISTINCT(p) FROM Post p JOIN FETCH p.tags", Post.class)
            .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
            .getResultList(); // 1
        // then
        posts.forEach(post -> {
            post.tags.forEach(tag -> System.out.println(tag.id));
        });
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1);
        assertThat(posts.size()).isEqualTo(n);
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
