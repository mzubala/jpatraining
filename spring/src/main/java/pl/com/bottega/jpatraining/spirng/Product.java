package pl.com.bottega.jpatraining.spirng;

import lombok.Getter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
@Getter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToOne(
        fetch = FetchType.LAZY,
        cascade = CascadeType.PERSIST
    )
    private Category category;

    public Product() {
    }

    public Product(String name, Category category) {
        this.name = name; this.category = category;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }
}
