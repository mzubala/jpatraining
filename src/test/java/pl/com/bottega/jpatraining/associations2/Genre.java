package pl.com.bottega.jpatraining.associations2;

import java.util.HashSet;
import java.util.Set;

public class Genre {

    private Long id;
    private String name;

    private Set<Movie> movies = new HashSet<>();

    private Genre() {}

    public Genre(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public Set<Movie> getMovies() {
        return movies;
    }
}
