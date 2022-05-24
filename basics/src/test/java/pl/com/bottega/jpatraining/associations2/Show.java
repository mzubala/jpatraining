package pl.com.bottega.jpatraining.associations2;

import java.time.Instant;

public class Show {

    private Long id;

    private Cinema cinema;

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
