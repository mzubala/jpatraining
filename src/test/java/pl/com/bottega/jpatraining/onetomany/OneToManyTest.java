package pl.com.bottega.jpatraining.onetomany;

import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.collection.internal.PersistentList;
import org.hibernate.collection.internal.PersistentSet;
import org.hibernate.jpa.QueryHints;
import org.junit.Test;
import pl.com.bottega.jpatraining.BaseJpaTest;

import java.util.List;

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
    public void duplicatesPostsWhenFetchingMultipleCollections() {
        // given
        savedPost();

        // when
        List<Post> posts = template.getEntityManager().createQuery("" +
            "FROM Post p " +
            "LEFT JOIN FETCH p.comments " +
            "LEFT JOIN FETCH p.likes")
        .getResultList();

        // then
        assertThat(posts.size()).isEqualTo(4L);
    }

    @Test
    public void deduplicatesPostsWhenFetchingMultipleCollections() {
        // given
        savedPost();

        // when
        List<Post> posts = template.getEntityManager().createQuery("" +
            "SELECT DISTINCT p " +
            "FROM Post p " +
            "LEFT JOIN FETCH p.comments " +
            "LEFT JOIN FETCH p.likes")
            .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
            .getResultList();

        // then
        assertThat(posts.size()).isEqualTo(1L);
    }

    @Test
    public void np1SelectProblem() {
        // given
        int n = 100;
        for(int i = 0; i<n; i++) {
            savedPost();
        }

        // when
        template.executeInTx((em) -> {
           List<Post> posts = em.createQuery("" +
               "SELECT DISTINCT p FROM Post p " +
               "LEFT JOIN FETCH p.comments")
               .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
               .getResultList();
           posts.forEach(p -> {
               System.out.println("First comment id = " + p.comments.get(0).id);
           });
        });

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1);
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
