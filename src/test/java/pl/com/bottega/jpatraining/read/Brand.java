package pl.com.bottega.jpatraining.read;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Brand {

    @Id
    @GeneratedValue
    Long id;

    String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "brand")
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
