package pl.com.bottega.jpatraining.associations2;

import java.util.HashSet;
import java.util.Set;

public class Actor {

    private Long id;
    private String firstName;
    private String lastName;

    private Set<Movie> stagedIn = new HashSet<>();

    private Actor() {
    }

    public Actor(Long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void addMovie(Movie movie) {
        this.stagedIn.add(movie);
    }

    public Long getId() {
        return id;
    }

    public Set<Movie> getStagedIn() {
        return stagedIn;
    }
}
