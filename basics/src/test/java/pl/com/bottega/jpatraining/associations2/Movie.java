package pl.com.bottega.jpatraining.associations2;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Movie {

    @Id
    @GeneratedValue(
        strategy = GenerationType.IDENTITY
    )
    private Long id;
    private String title;

    @ManyToMany
    private Set<Actor> actors = new HashSet<>();

    @ManyToMany
    private Set<Genre> genres = new HashSet<>();

    @OneToMany(mappedBy = "movie")
    private Set<Show> shows = new HashSet<>();

    public Movie(String title) {
        this.title = title;
    }

    Movie() {
    }

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
