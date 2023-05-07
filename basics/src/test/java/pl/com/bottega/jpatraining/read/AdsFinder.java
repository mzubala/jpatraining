package pl.com.bottega.jpatraining.read;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;

public class AdsFinder {

    private final EntityManager entityManager;

    public AdsFinder(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Brand brand(String name) {
        return null;
    }

    public Model model(String brandName, String modelName) {
        return null;
    }

    public int countByBrand(Brand brand) {
        return 0;
    }

    public List<CarAd> findByModelAndPrice(Model model, BigDecimal from, BigDecimal to) {
        return null;
    }

    public CarAdSearchResults search(CarAdQuery query) {
        return null;
    }

}
