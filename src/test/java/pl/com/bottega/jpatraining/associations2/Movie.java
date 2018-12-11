package pl.com.bottega.jpatraining.associations2;

import java.util.HashSet;
import java.util.Set;

public class Movie {

    private Long id;
    private String title;

    private Set<Actor> actors = new HashSet<>();

    private Set<Genre> genres = new HashSet<>();

    private Set<Show> shows = new HashSet<>();

    public Movie(String title) {
        this.title = title;
    }

    private Movie() {}

    public Set<Genre> getGenres() {
        return genres;
    }

    public Set<Actor> getActors() {
        return actors;
    }

    public Long getId() {
        return id;
    }

    public Set<Show> getShows() {
        return shows;
    }
}
