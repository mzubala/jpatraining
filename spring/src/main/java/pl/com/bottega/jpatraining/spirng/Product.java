package pl.com.bottega.jpatraining.spirng;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String productName;

    @ManyToMany(fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Attribute> attributes = new HashSet<>();

    public Product(String name, Set<Attribute> attributes) {
        this.productName = name;
        this.attributes = attributes;
    }
}
