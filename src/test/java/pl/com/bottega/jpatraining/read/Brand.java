package pl.com.bottega.jpatraining.read;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
@NamedQuery(
    name = Brand.FIND_BY_NAME,
    query = "FROM Brand b WHERE b.name = :brandName"
)
public class Brand {

    public static final String FIND_BY_NAME = "findByName";

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
