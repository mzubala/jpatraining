package pl.com.bottega.jpatraining.associations2;

import javax.persistence.Entity;
import javax.persistence.Id;

public class Cinema {

    private Long id;
    private String name;
    private String city;

    public Cinema(long id, String city, String name) {
        this.id = id;
        this.city = city;
        this.name = name;
    }

    private Cinema() {}

    public Long getId() {
        return id;
    }
}
