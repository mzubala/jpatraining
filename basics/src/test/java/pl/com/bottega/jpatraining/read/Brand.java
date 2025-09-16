package pl.com.bottega.jpatraining.read;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@NamedQuery(
    name = "countBrands",
    query = "SELECT COUNT(b) FROM Brand b"
)
public class Brand {

    @Id
    @GeneratedValue
    Long id;

    String name;

    @OneToMany(cascade = CascadeType.ALL)
    Set<Model> models = new HashSet<>();

    public Brand(String name) {
        this.name = name;
    }

    public Brand() {
    }

    public Model model(String name) {
        return models.stream().filter(m -> m.name.equals(name)).findFirst().get();
    }
}
