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
            fetchedPost.comments.add( new Comment(post));
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
    public void np1SelectProblem() {
        // given
        int n = 100;
        for(int i = 0; i<n; i++) {
            savedPost();
        }

        // when
        List<Post> postList = template.getEntityManager()
            .createQuery("SELECT DISTINCT(p) FROM Post p LEFT JOIN FETCH p.likes")
            .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
            .getResultList(); // 1
        for(Post post : postList) {
            System.out.println(post.likes.size()); // 1
        } // n

        // then
        assertThat(postList.size()).isEqualTo(n);
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1);
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
