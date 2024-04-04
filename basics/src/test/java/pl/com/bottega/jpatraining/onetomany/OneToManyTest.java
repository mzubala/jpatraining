package pl.com.bottega.jpatraining.onetomany;

import org.hibernate.collection.spi.PersistentBag;
import org.hibernate.collection.spi.PersistentList;
import org.hibernate.collection.spi.PersistentSet;
import org.hibernate.jpa.AvailableHints;
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
            fetchedPost.tags.add(new Tag(fetchedPost));
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
        assertThat(template.getEntityManager().createQuery("SELECT count(t) FROM Tag t", Long.class)
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
        var posts = template.getEntityManager().createQuery("SELECT p FROM Post p", Post.class).getResultList(); // 1
        for (Post post : posts) { // x n
            System.out.println(post.tags.size()); //  1
        } // n

        // then
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
        var posts = template.getEntityManager().createQuery("SELECT p FROM Post p JOIN FETCH p.tags", Post.class)
                .getResultList(); // 1
        for (Post post : posts) { // x 0
            System.out.println(post.tags.size()); //  0
        } // n

        // then
        assertThat(posts.size()).isEqualTo(n);
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1);
    }

    @Test
    public void np1SelectBatchSizeSolution() {
        // given
        int n = 100;
        for (int i = 0; i < n; i++) {
            savedPost();
        }

        // when
        var posts = template.getEntityManager().createQuery("SELECT p FROM Post p", Post.class).getResultList(); // 1
        for (Post post : posts) { // x n
            System.out.println(post.likes.size()); //  1
        } // n

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(n / 20 + 1);
    }

    @Test
    public void np1SelectEntityGraphSolution() {
        int n = 100;
        for (int i = 0; i < n; i++) {
            savedPost();
        }

        // when
        var graph = template.getEntityManager().getEntityGraph("all-dependencies");
        var posts = template.getEntityManager().createQuery("SELECT p FROM Post p", Post.class)
                .setHint(AvailableHints.HINT_SPEC_LOAD_GRAPH, graph)
                .getResultList(); // 1
        for (Post post : posts) { // x n
            System.out.println(post.comments.size()); //  1
            System.out.println(post.likes.size()); //  1
            System.out.println(post.tags.size()); //  1
        } // n

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1);
    }

    @Test
    public void np1SelectSolutionWithSeparateQueries() {
        int n = 100;
        for (int i = 0; i < n; i++) {
            savedPost();
        }

        // when
        var posts = template.getEntityManager().createQuery("SELECT p FROM Post p JOIN FETCH p.likes", Post.class)
                .getResultList(); // 1
        template.getEntityManager().createQuery("SELECT p FROM Post p JOIN FETCH p.tags", Post.class)
                .getResultList();
        template.getEntityManager().createQuery("SELECT p FROM Post p JOIN FETCH p.comments", Post.class)
                .getResultList();
        for (Post post : posts) { // x n
            System.out.println(post.comments.size()); //  1
            System.out.println(post.likes.size()); //  1
            System.out.println(post.tags.size()); //  1
        } // n

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(3);
    }

    @Test
    public void cannotFetchMultipleBags() {
        // expect
        assertThatThrownBy(() -> {
            template.getEntityManager().createQuery("SELECT p FROM Post p JOIN FETCH p.shares JOIN FETCH p.likes", Post.class).getResultList();
        }).hasCauseInstanceOf(MultipleBagFetchException.class);
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
