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
        cq.where(cb.equal(brand.get("name"), name)); // WHERE b.name = ''
        return entityManager.createQuery(cq).getResultStream().findFirst().orElse(null);
    }

    public Model model(String brandName, String modelName) {
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(Model.class);
        var model = cq.from(Model.class); // FROM Brand b
        cq.where(cb.equal(model.get("name"), modelName),
            cb.equal(model.get("brand").get("name"), brandName)
        ); // WHERE b.name = ''
        return entityManager.createQuery(cq).getResultStream().findFirst().orElse(null);
    }

    public int countByBrand(Brand brand) {
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(Long.class);
        var carAd = cq.from(CarAd.class);
        cq.select(cb.count(carAd)); // SELECT count(c)
        var model = carAd.join("model");
        cq.where(cb.equal(model.get("brand"), brand));
        return entityManager.createQuery(cq).getSingleResult().intValue();
    }

    public List<CarAd> findByModelAndPrice(Model model, BigDecimal from, BigDecimal to) {
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(CarAd.class);
        var carAd = cq.from(CarAd.class);
        cq.where(cb.equal(carAd.get("model"), model), cb.between(carAd.get("price"), from, to));
        return entityManager.createQuery(cq).getResultList();
    }

    public CarAdSearchResults search(CarAdQuery query) {
        var cb = entityManager.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        var carAd = cq.from(CarAd.class);
        var predicate = cb.conjunction();
        if (query.brand != null) {
            predicate = cb.and(predicate, cb.equal(carAd.get("model").get("brand").get("name"), query.brand));
        }
        if (query.damaged != null) {
            predicate = cb.and(predicate, cb.equal(carAd.get("damaged"), query.damaged));
        }
        if (query.fuel != null) {
            predicate = cb.and(predicate, cb.equal(carAd.get("fuel"), query.fuel));
        }
        if (query.firstOwner != null) {
            predicate = cb.and(predicate, cb.equal(carAd.get("firstOwner"), query.firstOwner));
        }
        cq.where(predicate);
        var result = new CarAdSearchResults();
        cq.select(cb.construct(CarAdDto.class, carAd.get("id"), carAd.get("model").get("brand").get("name"),
            carAd.get("model").get("name")
        ));
        result.ads = entityManager.createQuery(cq)
            .setFirstResult(query.perPage * (query.page - 1))
            .setMaxResults(query.perPage)
            .getResultList();
        cq.select(cb.count(carAd));
        result.totalCount = ((Long) entityManager.createQuery(cq).getSingleResult()).intValue();
        result.perPage = query.perPage;
        result.pageNumber = query.page;
        result.pagesCount = result.totalCount / result.perPage + (result.totalCount % result.perPage != 0 ? 1 : 0);
        return result;
    }

}
