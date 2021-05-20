package pl.com.bottega.jpatraining.associations2;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public class CinemaManager {
    private final EntityManager entityManager;

    public CinemaManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void createMovie(String title, List<Long> actorIds, List<Long> genreIds) {
       // TODO
       List<Actor> actors = actorIds.stream().map((actorId) -> entityManager.getReference(Actor.class, actorId)).collect(Collectors.toList());
    }

    public void createShow(Long movieId, Long cinemaId, Instant when) {
        // TODO
    }
}
