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
            fetchedPost.comments.add(new Comment(fetchedPost));
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
    public void np1Problem() {
        // given
        int n = 100;
        for (int i = 0; i < n; i++) {
            savedPost();
        }

        // when
        List<Post> fetchedPosts = template.getEntityManager().createQuery("SELECT p FROM Post p", Post.class).getResultList(); // 1 query
        for (Post post : fetchedPosts) {
            System.out.println(post.tags.size()); // 1 query
        } // n queries

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(n + 1);
    }

    @Test
    public void np1JoinFetchSolution() {
        // given
        int n = 100;
        for (int i = 0; i < n; i++) {
            savedPost();
        }

        // when
        List<Post> fetchedPosts = template.getEntityManager().createQuery("SELECT p FROM Post p LEFT JOIN FETCH p.tags", Post.class)
            //.setHint(QueryHints.PASS_DISTINCT_THROUGH, false) Hibernate 5
            .getResultList(); // 1 query
        for (Post post : fetchedPosts) {
            System.out.println(post.tags.size()); // 0 query
        } // 0 queries

        // then
        assertThat(fetchedPosts.size()).isEqualTo(n);
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1);
    }

    @Test
    public void np1BatchSizeSolution() {
        // given
        int n = 100;
        for (int i = 0; i < n; i++) {
            savedPost();
        }

        // when
        List<Post> fetchedPosts = template.getEntityManager().createQuery("SELECT p FROM Post p", Post.class)
            .getResultList(); // 1 query
        for (Post post : fetchedPosts) {
            System.out.println(post.likes.size());
        }

        // then
        assertThat(fetchedPosts.size()).isEqualTo(n);
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(n / Post.BATCH_SIZE + 1);
    }

    @Test
    public void multipleBagFetchException() {
        // expect
        template.getEntityManager().createQuery("SELECT p FROM Post p " +
            "LEFT JOIN FETCH p.likes " +
            "LEFT JOIN FETCH p.tags", Post.class
        ).getResultList();
        assertThatThrownBy(() -> {
            template.getEntityManager().createQuery("SELECT p FROM Post p " +
                "LEFT JOIN FETCH p.likes " +
                "LEFT JOIN FETCH p.shares", Post.class
            ).getResultList();
        }).hasCauseInstanceOf(MultipleBagFetchException.class);
    }

    @Test
    public void splitingQueriesWithMultipleJoins() {
        // given
        int n = 100;
        for (int i = 0; i < n; i++) {
            savedPost();
        }

        // when
        List<Post> fetchedPostsWithTags = template.getEntityManager().createQuery("SELECT p FROM Post p JOIN FETCH p.tags", Post.class)
            .getResultList(); // 1
        List<Post> fetchedPostsWithLikes = template.getEntityManager().createQuery("SELECT p FROM Post p JOIN FETCH p.likes", Post.class).getResultList(); // 1
        for (Post post : fetchedPostsWithTags) {
            System.out.println("Likes count = " + post.likes.size()); // 0
            System.out.println("Tags count = " + post.tags.size()); // 0
        }

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
