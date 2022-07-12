package pl.com.bottega.jpatraining.onetomany;

import org.hibernate.LazyInitializationException;
import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.collection.internal.PersistentList;
import org.hibernate.collection.internal.PersistentSet;
import org.hibernate.jpa.QueryHints;
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
            fetchedPost.comments.add(0, new Comment(fetchedPost));
        });

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(6L);
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
        assertThat(template.getEntityManager().createQuery("SELECT count(t) FROM Tag t")
            .getSingleResult()).isEqualTo(0L);
    }

    @Test
    public void failsToLazyLoadAfterEntityMangerIsClosed() {
        // given
        Post post = savedPost();

        // when
        Post fetchedPost = template.getEntityManager().find(Post.class, post.id);
        template.close();

        // then
        assertThatThrownBy(() -> fetchedPost.comments.get(0)).isInstanceOf(LazyInitializationException.class);
    }

    @Test
    public void np1SelectProblem() {
        // given
        int n = 50;
        for (int i = 0; i < n; i++) {
            savedPost();
        }

        // when
        List<Post> posts = template.getEntityManager().createQuery("SELECT p FROM Post p").getResultList();
        for (Post post : posts) {
            System.out.println(post.comments.get(0).id);
        }

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(n + 1);
    }

    @Test
    public void np1SelectProblemSolution() {
        // given
        int n = 50;
        for (int i = 0; i < n; i++) {
            savedPost();
        }

        // when
        List<Post> posts = template.getEntityManager().createQuery("SELECT DISTINCT p FROM Post p JOIN FETCH p.comments")
            .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
            .getResultList();
        for (Post post : posts) {
            System.out.println(post.comments.get(0).id);
        }

        // then
        assertThat(posts.size()).isEqualTo(n);
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1);
    }

    @Test
    public void cannotFetchTwoBags() {
        // expect
        assertThatThrownBy(() -> {
            template.getEntityManager().createQuery("SELECT p FROM Person p JOIN FETCH p.dogs JOIN FETCH p.cats").getResultList();
        }).hasCauseInstanceOf(MultipleBagFetchException.class);
    }

    @Test
    public void fetchMultipleBags() {
        // given
        int n = 50;
        for (int i = 0; i < n; i++) {
            var person = new Person();
            person.dogs.add(new Dog());
            person.dogs.add(new Dog());
            person.cats.add(new Cat());
            template.executeInTx((em) -> {
                em.persist(person);
            });
        }
        template.close();
        template.getStatistics().clear();

        // when
        var persons = template.getEntityManager().createQuery("SELECT p FROM Person p LEFT JOIN FETCH p.dogs", Person.class).getResultList();
        persons = template.getEntityManager().createQuery("SELECT p FROM Person p LEFT JOIN FETCH p.cats", Person.class).getResultList();
        persons.forEach(p -> {
            System.out.println(p.dogs.size());
            System.out.println(p.cats.size());
        });

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(2L);
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
