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
       // TODO
    }

    public void createShow(Long movieId, Long cinemaId, Instant when) {
        // TODO
    }
}
