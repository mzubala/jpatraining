package pl.com.bottega.jpatraining.onetomany;

import org.hibernate.collection.spi.PersistentBag;
import org.hibernate.collection.spi.PersistentList;
import org.hibernate.collection.spi.PersistentSet;
import org.hibernate.jpa.AvailableHints;
import org.hibernate.jpa.QueryHints;
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
            fetchedPost.likes.add(new Like(fetchedPost));
            //em.persist(new Like(em.getReference(Post.class, post.id)));
        });

        // then
        assertThat(template.getStatistics().getPrepareStatementCount())
            .isEqualTo(2);
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

        // when
        var fetchedPosts = template.getEntityManager().createQuery("SELECT p FROM Post p", Post.class)
            .getResultList(); // 1
        for (Post post : fetchedPosts) {
            System.out.println(post.tags.size()); // 1
        } // * n

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

        // when
        var fetchedPosts = template.getEntityManager()
            .createQuery("SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.tags", Post.class)
            //.setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList(); // 1
        for (Post post : fetchedPosts) {
            System.out.println(post.tags.size()); // 0
        } // * n

        // then
        assertThat(fetchedPosts).hasSize(n);
        //assertThat(fetchedPosts).hasSize(200); --> Hibernate 5
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1);
    }

    @Test
    public void np1SelectProblemBatchSizeSolution() {
        // given
        int n = 100;
        for (int i = 0; i < n; i++) {
            savedPost();
        }

        // when
        var fetchedPosts = template.getEntityManager().createQuery("SELECT p FROM Post p", Post.class)
            .getResultList(); // 1
        for (Post post : fetchedPosts) {
            System.out.println(post.likes.size());
        }

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1 + n / Post.LIKES_BATCH_SIZE);
    }

    @Test
    public void np1SelectProblemEntityGraphSolution() {
        // given
        int n = 100;
        for (int i = 0; i < n; i++) {
            savedPost();
        }

        // when
        var graph = template.getEntityManager().getEntityGraph(Post.POST_WITH_COMMENTS);
        var fetchedPosts = template.getEntityManager().createQuery("SELECT p FROM Post p", Post.class)
            .setHint(AvailableHints.HINT_SPEC_FETCH_GRAPH, graph)
            .getResultList(); // 1
        for (Post post : fetchedPosts) {
            System.out.println(post.comments.size());
        }

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1);
    }

    @Test
    void multipleBagFetchException() {
        // given
        savedPost();

        // expect
        template.getEntityManager().createQuery("SELECT p FROM Post p LEFT JOIN FETCH p.likes " +
                "LEFT JOIN FETCH p.tags",
            Post.class).getResultList();
        assertThatThrownBy(() -> {
            template.getEntityManager().createQuery("SELECT p FROM Post p LEFT JOIN FETCH p.likes " +
                    "LEFT JOIN FETCH p.shares",
                Post.class).getResultList();
        }).hasCauseInstanceOf(MultipleBagFetchException.class);
    }

    @Test
    void multipleBagFetchExceptionSolution() {
        // given
        int n = 100;
        for (int i = 0; i < n; i++) {
            savedPost();
        }

        // when
        var postsWithLikes = template.getEntityManager().createQuery("SELECT p FROM Post p LEFT JOIN FETCH p.likes", Post.class)
            .getResultList();
        var postsWithWithShares = template.getEntityManager().createQuery("SELECT p FROM Post p LEFT JOIN FETCH p.shares", Post.class)
            .getResultList();
        for(Post post : postsWithLikes) {
            System.out.println(post.shares.size());
            System.out.println(post.likes.size());
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
        post.shares.add(new Share());
        return post;
    }

}
