package pl.com.bottega.jpatraining.read;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;

public class AdsFinder {

    private final EntityManager entityManager;

    public AdsFinder(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Brand brand(String name) {
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(Brand.class);
        var brand = cq.from(Brand.class); // FROM Brand b
        cq.select(brand); // SELECT b
        cq.where(cb.equal(brand.get("name"), cb.parameter(String.class, "name"))); // b.name = :name
        return entityManager.createQuery(cq).setParameter("name", name).getResultStream().findFirst().orElse(null);
    }

    public Model model(String brandName, String modelName) {
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(Model.class);
        var model = cq.from(Model.class); // FROM Model m
        cq.select(model); // SELECT m
        cq.where(
            cb.equal(model.get("name"), cb.parameter(String.class, "modelName")),
            cb.equal(model.get("brand").get("name"), cb.parameter(String.class, "brandName"))
        );
        return entityManager.createQuery(cq).setParameter("modelName", modelName)
            .setParameter("brandName", brandName)
            .getResultStream().findFirst().orElse(null);
    }

    public int countCarAdsByBrand(Brand brand) {
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(Long.class);
        var carAd = cq.from(CarAd.class); // FROM Brand b
        return entityManager.createQuery(
            cq.select(cb.count(carAd))
                .where(cb.equal(carAd.get("model").get("brand"), brand))
        ).getSingleResult().intValue();
    }

    public List<CarAd> findByModelAndPrice(Model model, BigDecimal from, BigDecimal to) {
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(CarAd.class);
        var carAd = cq.from(CarAd.class); // FROM Brand b
        return entityManager.createQuery(
            cq.select(carAd)
                .where(
                    cb.equal(carAd.get("model"), model),
                    cb.between(carAd.get("price"), from, to)
                )
        ).getResultList();
    }

    public CarAdSearchResults search(CarAdQuery query) {
        var cb = entityManager.getCriteriaBuilder();
        var dtoQuery = cb.createQuery(CarAdDto.class);
        var carAd = dtoQuery.from(CarAd.class);
        dtoQuery.select(cb.construct(CarAdDto.class, carAd.get("id"), carAd.get("model").get("brand").get("name"), carAd.get("model").get("name"))); // SELECT new CarAdDto(....)
        dtoQuery.where(createPredicate(cb, query, carAd));
        dtoQuery.orderBy(cb.asc(carAd.get("id")));

        var contQuery = cb.createQuery(Long.class);
        var countRoot = contQuery.from(CarAd.class);
        contQuery.select(cb.count(countRoot));
        contQuery.where(createPredicate(cb, query, countRoot));

        var results = new CarAdSearchResults();
        results.ads = entityManager.createQuery(dtoQuery)
            .setMaxResults(query.perPage) // LIMIT
            .setFirstResult((query.page - 1) * query.perPage) // OFFSET
            .getResultList();
        results.perPage = query.perPage;
        results.pageNumber = query.page;
        results.totalCount = entityManager.createQuery(contQuery).getSingleResult().intValue();
        results.pagesCount = results.totalCount / results.perPage + (results.totalCount % results.perPage > 0 ? 1 : 0);
        return results;
    }

    private Predicate createPredicate(CriteriaBuilder cb, CarAdQuery query, Root<CarAd> carAd) {
        var predicate = cb.conjunction();
        if(query.brand != null) {
            predicate = cb.and(predicate, cb.equal(carAd.get("model").get("brand").get("name"), query.brand));
        }
        if(query.damaged != null) {
            predicate = cb.and(predicate, cb.equal(carAd.get("damaged"), query.damaged));
        }
        if(query.fuel != null) {
            predicate = cb.and(predicate, cb.equal(carAd.get("fuel"), query.fuel));
        }
        if(query.firstOwner != null) {
            predicate = cb.and(predicate, cb.equal(carAd.get("firstOwner"), query.firstOwner));
        }
        return predicate;
    }

}
