package pl.com.bottega.jpatraining.read;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import java.math.BigDecimal;
import java.util.List;

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
        cq.where(cb.equal(brand.get("name"), name)); // WHERE b.name = 'name'
        return entityManager.createQuery(cq).getResultStream().findFirst().orElse(null);
    }

    public Model model(String brandName, String modelName) {
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(Model.class);
        var model = cq.from(Model.class); // FROM Model m
        cq.select(model); // SELECT m
        var brand = model.join("brand");
        cq.where(
            cb.equal(brand.get("name"), brandName),
            cb.equal(model.get("name"), modelName)
        ); // WHERE b.name = 'name'
        return entityManager.createQuery(cq).getResultStream().findFirst().orElse(null);
    }

    public int countByBrand(Brand brand) {
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(Long.class);
        var carAd = cq.from(CarAd.class);
        cq.select(cb.count(carAd)); // SELECT count(carAd)
        var model = carAd.join("model");
        cq.where(cb.equal(model.get("brand"), brand));
        return entityManager.createQuery(cq).getSingleResult().intValue();
    }

    public List<CarAd> findByModelAndPrice(Model model, BigDecimal from, BigDecimal to) {
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(CarAd.class);
        var carAd = cq.from(CarAd.class);
        cq.select(carAd); // SELECT carAd
        cq.where(
            cb.equal(carAd.get("model"), model),
            cb.between(carAd.get("price"), from, to)
        );
        return entityManager.createQuery(cq).getResultList();
    }

    public CarAdSearchResults search(CarAdQuery query) {
        var cb = entityManager.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        var carAd = cq.from(CarAd.class);
        var model = carAd.join("model");
        var brand = model.join("brand");
        var predicate = cb.conjunction();
        if (query.fuel != null) {
            predicate = cb.and(predicate, cb.equal(carAd.get("fuel"), query.fuel));
        }
        if (query.brand != null) {
            predicate = cb.and(predicate, cb.equal(brand.get("name"), query.brand));
        }
        if (query.model != null) {
            predicate = cb.and(predicate, cb.equal(model.get("name"), query.model));
        }
        if (query.damaged != null) {
            predicate = cb.and(predicate, cb.equal(carAd.get("damaged"), query.damaged));
        }
        if (query.firstOwner != null) {
            predicate = cb.and(predicate, cb.equal(carAd.get("firstOwner"), query.firstOwner));
        }
        cq.where(predicate);

        CarAdSearchResults results = new CarAdSearchResults();
        cq.select(cb.construct(CarAdDto.class, carAd.get("id"), brand.get("name"), model.get("name")));
        results.ads = entityManager.createQuery(cq).setMaxResults(query.perPage).setFirstResult((query.page - 1) * query.perPage).getResultList();
        cq.select(cb.count(carAd));
        results.totalCount = ((Long) entityManager.createQuery(cq).getSingleResult()).intValue();
        results.pageNumber = query.page;
        results.perPage = query.perPage;
        results.pagesCount = results.totalCount / results.perPage + (results.totalCount % results.perPage > 0 ? 1 : 0);

        return results;
    }

}
