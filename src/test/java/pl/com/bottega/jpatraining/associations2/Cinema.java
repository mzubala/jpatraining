package pl.com.bottega.jpatraining.associations2;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Cinema {

    @Id
    private Long id;
    private String name;
    private String city;

    public Cinema(long id, String city, String name) {
        this.id = id;
        this.city = city;
        this.name = name;
    }

    Cinema() {}

    public Long getId() {
        return id;
    }
}
