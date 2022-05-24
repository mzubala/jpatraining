package pl.com.bottega.jpatraining.read;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Model {

    @Id
    @GeneratedValue
    Long id;

    String name;

    @ManyToOne
    private Brand brand;

    public Model(String name, Brand brand) {
        this.name = name;
        this.brand = brand;
    }

    public Model() {
    }
}
