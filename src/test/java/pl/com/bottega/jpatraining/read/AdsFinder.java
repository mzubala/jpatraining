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
        Join<CarAd, Model> model = carAd.join("model");
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
        CriteriaQuery cq = cb.createQuery();

        Root<CarAd> carAdd = cq.from(CarAd.class);
        Join<CarAd, Model> model = carAdd.join("model");
        Join<Model, Brand> brand = model.join("brand");
        cq.select(cb.construct(CarAdDto.class, carAdd.get("id"), brand.get("name"), model.get("name")));
        cq.where(createPredicate(query, cb, carAdd, brand));

        TypedQuery<CarAdDto> carAdDtoQuery = entityManager.createQuery(cq)
            .setMaxResults(query.perPage).setFirstResult((query.page - 1) * query.perPage);
        List<CarAdDto> carAdDtos = carAdDtoQuery.getResultList();
        CarAdSearchResults results = new CarAdSearchResults();
        results.ads = carAdDtos;
        results.pageNumber = query.page;
        results.perPage = query.perPage;
        cq.select(cb.count(carAdd));
        int count = ((Long) entityManager.createQuery(cq).getSingleResult()).intValue();
        results.totalCount = count;
        results.pagesCount = count / query.perPage + (count % query.perPage > 0 ? 1 : 0);
        return results;
    }

    private Predicate createPredicate(CarAdQuery query, CriteriaBuilder cb, Root<CarAd> carAdd, Join<Model, Brand> brand) {
        Predicate predicate = cb.conjunction();
        if (query.brand != null) {
            predicate = cb.and(predicate, cb.equal(brand.get("name"), query.brand));
        }
        if (query.fuel != null) {
            predicate = cb.and(predicate, cb.equal(carAdd.get("fuel"), query.fuel));
        }
        if (query.firstOwner != null) {
            predicate = cb.and(predicate, cb.equal(carAdd.get("firstOwner"), query.firstOwner));
        }
        if (query.damaged != null) {
            predicate = cb.and(predicate, cb.equal(carAdd.get("damaged"), query.damaged));
        }
        return predicate;
    }

}
