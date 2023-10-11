package pl.com.bottega.jpatraining.associations2;

import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public class CinemaManager {
    private final EntityManager entityManager;

    public CinemaManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void createMovie(String title, List<Long> actorIds, List<Long> genreIds) {
        Movie movie = new Movie(title);
        movie.getActors().addAll(getReferences(actorIds, Actor.class));
        movie.getGenres().addAll(getReferences(genreIds, Genre.class));
        entityManager.persist(movie);
    }

    public void createShow(Long movieId, Long cinemaId, Instant when) {
        Show show = new Show(entityManager.getReference(Cinema.class, cinemaId), entityManager.getReference(Movie.class, movieId), when);
        entityManager.persist(show);
    }

    private <T> List<T> getReferences(List<Long> ids, Class<T> entityClass) {
        return ids.stream().map((id) -> entityManager.getReference(entityClass, id)).collect(Collectors.toList());
    }
}
