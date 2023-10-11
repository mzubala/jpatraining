package pl.com.bottega.jpatraining.associations2;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.Instant;

@Entity
public class Show {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Cinema cinema;

    @ManyToOne
    private Movie movie;

    private Instant when;

    public Show(Cinema cinema, Movie movie, Instant when) {
        this.cinema = cinema;
        this.movie = movie;
        this.when = when;
    }

    Show() {}

    public Long getId() {
        return id;
    }

    public Cinema getCinema() {
        return cinema;
    }

    public Movie getMovie() {
        return movie;
    }

    public Instant getWhen() {
        return when;
    }
}
