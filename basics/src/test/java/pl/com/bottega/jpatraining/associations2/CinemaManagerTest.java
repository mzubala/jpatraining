package pl.com.bottega.jpatraining.associations2;

import org.junit.jupiter.api.Test;
import pl.com.bottega.jpatraining.BaseJpaTest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CinemaManagerTest extends BaseJpaTest {

    @Test
    public void savesActors() {
        // when
        setupActors();

        // then
        assertThat(template.getEntityManager().createQuery("SELECT count(a) FROM Actor a").getSingleResult()).isEqualTo(4L);
    }

    @Test
    public void savesGenres() {
        // when
        setupGenres();

        // then
        assertThat(template.getEntityManager().createQuery("SELECT count(g) FROM Genre g").getSingleResult()).isEqualTo(3L);
    }

    @Test
    public void createsMovies() {
        // given
        setupActors();
        setupGenres();
        CinemaManager cm = new CinemaManager(template.getEntityManager());
        template.getStatistics().clear();

        // when
        template.executeInTx(em -> {
            cm.createMovie("Psy 3", Arrays.asList(1L, 2L), Arrays.asList(1L, 3L));
        });
        template.close();

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(5);
        template.executeInTx(em -> {
            Movie movie = (Movie) em.createQuery("SELECT m FROM Movie m WHERE m.title = 'Psy 3'").getSingleResult();
            assertThat(movie.getGenres().stream().map(Genre::getId)).containsOnly(1L, 3L);
            assertThat(movie.getActors().stream().map(Actor::getId)).containsOnly(1L, 2L);
            movie.getGenres().forEach(g -> {
                assertThat(g.getMovies()).contains(movie);
            });
            movie.getActors().forEach(a -> {
                assertThat(a.getStagedIn()).contains(movie);
            });
        });
    }

    @Test
    public void savesCinemas() {
        // when
        setupCinemas();

        // then
        assertThat(template.getEntityManager().createQuery("SELECT count(c) FROM Cinema c").getSingleResult()).isEqualTo(2L);
    }

    @Test
    public void createsShows() {
        // given
        setupGenres();
        setupActors();
        setupMovies();
        setupCinemas();
        template.getStatistics().clear();

        // when
        Instant when = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        template.executeInTx(em -> {
            CinemaManager cm = new CinemaManager(em);
            cm.createShow(movies.get(0).getId(), 1L, when);
            cm.createShow(movies.get(0).getId(), 2L, when);
            cm.createShow(movies.get(0).getId(), 2L, when);
        });
        template.close();

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(3);
        template.executeInTx(em -> {
            Movie movie = em.find(Movie.class, movies.get(0).getId());
            assertThat(movie.getShows().size()).isEqualTo(3);
            assertThat(movie.getShows().stream().map(Show::getCinema).map(Cinema::getId)).containsOnly(1L, 2L, 2L);
            assertThat(movie.getShows().stream().map(Show::getWhen)).containsExactly(when, when, when);
        });
    }

    private void setupActors() {
        template.executeInTx(em -> {
            em.persist(new Actor(1L, "Cezary", "Pazura"));
            em.persist(new Actor(2L, "Boguslaw", "Linda"));
            em.persist(new Actor(3L, "Uma", "Thurman"));
            em.persist(new Actor(4L, "Samuel L.", "Jackson"));
        });
        template.close();
    }

    private void setupGenres() {
        template.executeInTx(em -> {
            em.persist(new Genre(1L, "Drama"));
            em.persist(new Genre(2L, "Crime"));
            em.persist(new Genre(3L, "Comedy"));
        });
        template.close();
    }

    private void setupCinemas() {
        template.executeInTx(em -> {
            em.persist(new Cinema(1L, "Lublin", "Plaza"));
            em.persist(new Cinema(2L, "Warszawa", "Arkadia"));
        });
    }

    private List<Movie> movies;

    private void setupMovies() {
        template.executeInTx(em -> {
            CinemaManager cm = new CinemaManager(template.getEntityManager());
            cm.createMovie("Psy 3", Arrays.asList(1L, 2L), Arrays.asList(1L, 3L));
            cm.createMovie("Pulp Fiction", Arrays.asList(3L, 4L), Arrays.asList(1L, 2L));
            movies = em.createQuery("FROM Movie m").getResultList();
        });
        template.close();
    }

}
