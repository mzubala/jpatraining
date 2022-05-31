package pl.com.bottega.jpatraining.associations2;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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