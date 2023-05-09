package pl.com.bottega.jpatraining.read;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaQuery;

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
        var brand = cq.from(Brand.class); // FROM Brand brand
        cq.select(brand);
        cq.where(cb.equal(brand.get("name"), name));
        return entityManager.createQuery(cq).getResultStream().findFirst().orElse(null);
    }

    public Model model(String brandName, String modelName) {
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(Model.class);
        var model = cq.from(Model.class);
        var brand = model.join("brand");
        cq.select(model);
        cq.where(
            cb.equal(brand.get("name"), brandName),
            cb.equal(model.get("name"), modelName)
        );
        return entityManager.createQuery(cq).getResultStream().findFirst().orElse(null);
    }

    public int countByBrand(Brand brand) {
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(Long.class);
        var carAd = cq.from(CarAd.class);
        var model = carAd.join("model");
        cq.select(cb.count(carAd));
        cq.where(
           cb.equal(model.get("brand"), brand)
        );
        return entityManager.createQuery(cq).getSingleResult().intValue();
    }

    public List<CarAd> findByModelAndPrice(Model model, BigDecimal from, BigDecimal to) {
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(CarAd.class);
        var carAd = cq.from(CarAd.class);
        cq.select(carAd);
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

        var predicate = cb.conjunction();
        if(query.damaged != null) {
            predicate = cb.and(predicate,
                cb.equal(carAd.get("damaged"), query.damaged)
            );
        }
        if(query.brand != null) {
            // TODO
        }
        if(query.firstOwner != null) {
            // TODO
        }
        if(query.fuel != null) {
            // TODO
        }
        cq.where(predicate);
        // TODO cq.select(cb.construct(//.....));
        var result = new CarAdSearchResults();
        result.ads = (List<CarAdDto>) entityManager.createQuery(cq)
            //TODO .setFirstResult()
            //TODO .setMaxResults()
            .getResultList();
        result.pageNumber = query.page;
        result.perPage = query.perPage;

        // TODO cq.select(cb.count(...));
        // TODO result.totalCount = ???
        // TODO result.pagesCount = ???

        return result;
    }

}
