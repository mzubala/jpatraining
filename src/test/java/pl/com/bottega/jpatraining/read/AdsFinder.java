package pl.com.bottega.jpatraining.read;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;

public class AdsFinder {

    private final EntityManager entityManager;

    public AdsFinder(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    // JPQL
    public Brand brand(String name) {
        return entityManager.createNamedQuery(Brand.FIND_BY_NAME, Brand.class)
            .setParameter("brandName", name)
            .getSingleResult();
    }

    // JPQL
    public Model model(String brandName, String modelName) {
        return entityManager
            .createNamedQuery(Model.FIND_BY_NAME_AND_BRAND, Model.class)
            .setParameter("brandName", brandName)
            .setParameter("modelName", modelName)
            .getSingleResult();
    }

    // JPQL
    public int countByBrand(Brand brand) {
        return 0;
    }

    // JPQL
    public List<CarAd> findByModelAndPrice(Model model, BigDecimal from, BigDecimal to) {
        return null;
    }

    // Criteria API
    public CarAdSearchResults search(CarAdQuery query) {
        return null;
    }

}
