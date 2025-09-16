package pl.com.bottega.jpatraining.associations2;

import jakarta.persistence.EntityManager;

import java.time.Instant;
import java.util.List;

public class CinemaManager {
    private final EntityManager entityManager;

    public CinemaManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void createMovie(String title, List<Long> actorIds, List<Long> genreIds) {
        var movie = new Movie(title);
        movie.getActors().addAll(actorIds.stream().map(actorId -> entityManager.getReference(Actor.class, actorId)).toList());
        movie.getGenres().addAll(genreIds.stream().map(genreId -> entityManager.getReference(Genre.class, genreId)).toList());
        entityManager.persist(movie);
    }

    public void createShow(Long movieId, Long cinemaId, Instant when) {
        var show = new Show();
        show.setMovie(entityManager.getReference(Movie.class, movieId));
        show.setCinema(entityManager.getReference(Cinema.class, cinemaId));
        show.setWhen(when);
        entityManager.persist(show);
    }
}
