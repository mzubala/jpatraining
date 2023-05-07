package pl.com.bottega.jpatraining.read;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

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
