package pl.com.bottega.jpatraining.spirng;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@NamedQuery(name = "y", query = "SELECT p FROM Product p WHERE p.name LIKE :pattern")
class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToMany
    @JoinColumn
    Set<Category> categories;

    String name;

}
