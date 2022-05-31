package pl.com.bottega.jpatraining.associations2;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Actor {

    @Id
    private Long id;
    private String firstName;
    private String lastName;

    @ManyToMany(mappedBy = "actors")
    private Set<Movie> stagedIn = new HashSet<>();

    Actor() {
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