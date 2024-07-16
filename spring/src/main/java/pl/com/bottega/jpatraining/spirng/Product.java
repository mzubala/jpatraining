package pl.com.bottega.jpatraining.spirng;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Entity
@Data
class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(cascade = CascadeType.ALL)
    private Collection<Category> categories = new LinkedList<>();

    protected Product() {}

    public Product(String name) {
        this.name = name;
        this.categories.addAll(List.of(new Category("Test 1"), new Category("Test 2")));
    }
}
