package pl.com.bottega.jpatraining.read;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
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
