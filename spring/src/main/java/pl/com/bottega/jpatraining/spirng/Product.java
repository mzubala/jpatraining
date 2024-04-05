package pl.com.bottega.jpatraining.spirng;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Category> categories = new HashSet<>();
    private String name;

    public Product(Set<Category> categories) {
        this.categories = categories;
    }

    public Product() {
    }

    public void setName(String name) {
        this.name = name;
    }
}
