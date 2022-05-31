package pl.com.bottega.jpatraining.spirng;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public Product() {
    }

    public Product(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
