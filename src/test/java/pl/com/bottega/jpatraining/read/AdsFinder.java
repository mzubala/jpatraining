package pl.com.bottega.jpatraining.read;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);

        Root<CarAd> carAd = cq.from(CarAd.class);
        cq.select(cb.count(carAd));
        Join<CarAd, Model>  model = carAd.join("model");
        cq.where(cb.equal(model.get("brand"), brand));

        TypedQuery<Long> query = entityManager.createQuery(cq);
        return query.getSingleResult().intValue();
    }

    // JPQL
    public List<CarAd> findByModelAndPrice(Model model, BigDecimal from, BigDecimal to) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<CarAd> cq = cb.createQuery(CarAd.class);

        Root<CarAd> carAd = cq.from(CarAd.class); // FROM CarAd carAd
        cq.select(carAd); // SELECT carAd
        cq.where(
            cb.equal(carAd.get("model"), model), // cardAd.model = :model
            //cb.between(carAd.get("price"), from, to) // carAd.price BETWEEN :from AND :to
            cb.greaterThanOrEqualTo(carAd.get("price"), from),
            cb.lessThanOrEqualTo(carAd.get("price"), to)
        );

        TypedQuery<CarAd> query = entityManager.createQuery(cq);
        return query.getResultList();
    }

    // Criteria API
    public CarAdSearchResults search(CarAdQuery query) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<CarAdDto> cq = cb.createQuery(CarAdDto.class);

        Root<CarAd> carAdd = cq.from(CarAd.class);
        Join<CarAd, Model> model = carAdd.join("model");
        Join<Model, Brand> brand = model.join("brand");
        cq.select(cb.construct(CarAdDto.class, carAdd.get("id"), brand.get("name"), model.get("name")));
        Predicate predicate = cb.conjunction();
        if(query.brand != null) {
            predicate = cb.and(predicate, cb.equal(brand.get("name"), query.brand));
        }
        if(query.fuel != null) {
            predicate = cb.and(predicate, cb.equal(carAdd.get("fuel"), query.fuel));
        }
        cq.where(predicate);

        TypedQuery<CarAdDto> carAdDtoQuery = entityManager.createQuery(cq);
        List<CarAdDto> carAdDtos = carAdDtoQuery.getResultList();
        CarAdSearchResults results = new CarAdSearchResults();
        results.ads = carAdDtos;
        return results;
    }

}
