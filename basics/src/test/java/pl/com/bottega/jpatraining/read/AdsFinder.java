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
        cq.select(brand); // SELECT brand
        cq.where(cb.equal(brand.get("name"), name)); // WHERE brand.name = :name
        return entityManager.createQuery(cq).getResultStream().findFirst().orElse(null);
    }

    public Model model(String brandName, String modelName) {
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(Model.class);
        var model = cq.from(Model.class); // FROM Model model
        var brand = model.join("brand"); // JOIN model.brand brand
        cq.select(model); // SELECT model
        cq.where(cb.equal(brand.get("name"), brandName),
            cb.equal(model.get("name"), modelName)
        ); // WHERE brand.name = :brandName AND model.name = :modelName
        return entityManager.createQuery(cq).getResultStream().findFirst().orElse(null);
    }

    public int countByBrand(Brand brand) {
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(Long.class);
        var carAd = cq.from(CarAd.class); // FROM CarAd carAd
        var model = carAd.join("model"); // JOIN carAd.model model
        cq.select(cb.count(carAd)); // SELECT count(carAd)
        cq.where(cb.equal(model.get("brand"), brand)); // WHERE model.brand = :brand
        return entityManager.createQuery(cq).getSingleResult().intValue();
    }

    public List<CarAd> findByModelAndPrice(Model model, BigDecimal from, BigDecimal to) {
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(CarAd.class);
        var carAd = cq.from(CarAd.class); // FROM CarAd carAd
        cq.select(carAd); // SELECT carAd
        cq.where(cb.equal(carAd.get("model"), model),
            cb.between(carAd.get("price"), from, to)
        ); // WHERE carAd.model = :model AND carAd.price BETWEEN :from AND :to
        return entityManager.createQuery(cq).getResultList();
    }

    public CarAdSearchResults search(CarAdQuery query) {
        var cb = entityManager.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        var carAd = cq.from(CarAd.class);
        var predicate = cb.conjunction();
        if (query.firstOwner != null) {
            predicate = cb.and(predicate, cb.equal(carAd.get("firstOwner"), query.firstOwner));
        }
        if (query.brand != null) {
            // TODO
        }
        if (query.damaged != null) {
            // TODO
        }
        if (query.fuel != null) {
            // TODO
        }
        cq.where(predicate);
        cq.multiselect(cb.construct(CarAdDto.class, carAd.get("id"), carAd.get("model").get("brand").get("name"),
            carAd.get("model").get("name")
        )); // SELECT new MyDto(.....)
        cq.orderBy(cb.asc(carAd.get("id")));
        var result = new CarAdSearchResults();
        result.ads = entityManager.createQuery(cq)
            // TODO .setMaxResults()
            // TODO .setFirstResult()
            .getResultList();
        cq.multiselect(cb.count(carAd));
        cq.orderBy(List.of());
        result.totalCount = ((Long) entityManager.createQuery(cq).getSingleResult()).intValue();
        result.perPage = query.perPage;
        result.pageNumber = query.page;
        // TODO result.pagesCount = ???
        return result;
    }

}
