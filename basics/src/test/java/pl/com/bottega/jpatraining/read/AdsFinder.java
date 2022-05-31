package pl.com.bottega.jpatraining.read;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.List;

public class AdsFinder {

    private final EntityManager entityManager;

    public AdsFinder(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Brand brand(String name) {
        return entityManager.createQuery("Select b FROM Brand b WHERE b.name = :name", Brand.class)
            .setParameter("name", name)
            .getResultStream()
            .findFirst().orElse(null);
    }

    public Model model(String brandName, String modelName) {
        return entityManager.createQuery("Select m FROM Model m join m.brand b WHERE m.name=:modelName and b.name=:brandName", Model.class)
            .setParameter("modelName", modelName)
            .setParameter("brandName", brandName)
            .getResultStream()
            .findFirst().orElse(null);
    }

    public int countAdsByBrand(Brand brand) {
        return entityManager.createQuery("SELECT COUNT(ca) FROM CarAd ca join ca.model m where m.brand = :brand", Long.class)
            .setParameter("brand", brand)
            .getSingleResult().intValue();
    }

    public List<CarAd> findByModelAndPrice(Model model, BigDecimal from, BigDecimal to) {
        return entityManager.createQuery("SELECT ca FROM CarAd ca where ca.model = :model and ca.price between :from and :to", CarAd.class)
            .setParameter("model", model)
            .setParameter("from", from)
            .setParameter("to", to)
            .getResultList();
    }

    public CarAdSearchResults search(CarAdQuery query) {
        return null;
    }

}
