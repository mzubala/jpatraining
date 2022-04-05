package pl.com.bottega.jpatraining.associations2;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Genre {

    @Id
    private Long id;
    private String name;

    @ManyToMany(mappedBy = "genres")
    private Set<Movie> movies = new HashSet<>();

    Genre() {}

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
