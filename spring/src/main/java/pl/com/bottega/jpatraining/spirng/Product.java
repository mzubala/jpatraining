package pl.com.bottega.jpatraining.spirng;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
class Product {

    @Id
    private UUID id = UUID.randomUUID();

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private Set<Category> categories = new HashSet<>();

    private String name = "Test";

    public Set<Category> getCategories() {
        return categories;
    }

    public String getName() {
        return name;
    }
}
