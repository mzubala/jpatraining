package pl.com.bottega.jpatraining.read;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;

@Entity
@NamedQuery(
    name = Model.FIND_BY_NAME_AND_BRAND,
    query = "SELECT m FROM Model m " +
        "JOIN m.brand b " +
        "WHERE m.name = :modelName AND b.name = :brandName"
)
public class Model {

    public static final String FIND_BY_NAME_AND_BRAND = "findByNameAndBrand";

    @Id
    @GeneratedValue
    Long id;

    String name;

    @ManyToOne
    private Brand brand;

    public Model(String name, Brand brand) {
        this.name = name;
        this.brand = brand;
    }

    public Model() {
    }
}
