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
        var brand = cq.from(Brand.class); // FROM Brand b
        cq.select(brand); // SELECT brand
        cq.where(cb.equal(brand.get("name"), name)); // WHERE brand.name = 'xxxx'
        return entityManager.createQuery(cq).getResultStream().findFirst().orElse(null);
    }

    public Model model(String brandName, String modelName) {
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(Model.class);
        var model = cq.from(Model.class); // FROM Model model
        var brand = model.join("brand");
        cq.select(model); // SELECT model
        cq.where(
                cb.equal(model.get("name"), modelName),
                cb.equal(brand.get("name"), brandName) // model.brand.name = 'xxxx'
        ); // WHERE model.name = 'xxxx' AND ...
        return entityManager.createQuery(cq).getResultStream().findFirst().orElse(null);
    }

    public int countCarAdsByBrand(Brand brand) {
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(Long.class);
        var carAd = cq.from(CarAd.class); // FROM CarAd cad
        cq.select(cb.count(carAd)); // SELECT count(cad)
        cq.where(cb.equal(carAd.get("model").get("brand"), brand)); // WHERE carAd.model.brand = ?
        return entityManager.createQuery(cq).getSingleResult().intValue();
    }

    public List<CarAd> findByModelAndPrice(Model model, BigDecimal from, BigDecimal to) {
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(CarAd.class);
        var carAd = cq.from(CarAd.class); // FROM CarAd cad
        cq.select(carAd); // SELECT count(cad)
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
        if (query.brand != null) {
            predicate = cb.and(predicate, cb.equal(carAd.get("model").get("brand").get("name"), query.brand));
        }
        if (query.damaged != null) {
            // TODO
        }
        if (query.firstOwner != null) {
            // TODO
        }
        if (query.fuel != null) {
            // TODO
        }
        cq.where(predicate);
        cq.multiselect(cb.construct(CarAdDto.class, carAd.get("id"), carAd.get("model").get("brand").get("name"), carAd.get("model").get("name")));
        cq.orderBy(cb.asc(carAd.get("id")));
        var results = new CarAdSearchResults();
        results.ads = entityManager.createQuery(cq)
                .setFirstResult(query.perPage * (query.page - 1)) // TODO
                .setMaxResults(0) // TODO
                .getResultList();
        cq.orderBy(List.of());
        cq.multiselect(cb.count(carAd));
        results.totalCount = ((Long) entityManager.createQuery(cq).getSingleResult()).intValue();
        results.perPage = query.perPage;
        results.pageNumber = query.page;
        //results.pagesCount = (results.totalCount + results.perPage - 1) / results.perPage;
        //results.pagesCount = results.totalCount / results.perPage + (results.totalCount % results.perPage == 0 ? 0 : 1);
        results.pagesCount = (int) Math.ceil((double) results.totalCount / results.perPage);
        return results;
    }

}
