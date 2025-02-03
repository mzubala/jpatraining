package pl.com.bottega.jpatraining.read;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;

import java.math.BigDecimal;
import java.util.List;

public class AdsFinder {

    private final EntityManager entityManager;

    public AdsFinder(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Brand brand(String name) {
//        return entityManager.createQuery("SELECT b FROM Brand b WHERE b.name = :brandName", Brand.class)
//         .setParameter("brandName", name).getResultStream().findFirst().orElse(null);


        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(Brand.class);
        var brand = cq.from(Brand.class); // FROM Brand brand
        cq.select(brand); // SELECT brand
        cq.where(cb.equal(brand.get("name"), name)); // brand.name = xxxx
        return entityManager.createQuery(cq).getResultStream().findFirst().orElse(null);
    }

    public Model model(String brandName, String modelName) {
//        return entityManager.createQuery("SELECT m FROM Model m "
//                        + "WHERE m.brand.name = :brandName AND m.name = :modelName", Model.class)
//                .setParameter("brandName", brandName)
//                .setParameter("modelName", modelName)
//                .getResultStream().findFirst().orElse(null);
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(Model.class);
        var model = cq.from(Model.class); // FROM Brand brand
        cq.select(model); // SELECT model
        var brand = model.join("brand", JoinType.LEFT); // JOIN model.brand brand
        cq.where(
                cb.equal(model.get("name"), modelName), // model.name = xxxx
                cb.equal(brand.get("name"), brandName) // brand.name = xxxx
        );
        return entityManager.createQuery(cq).getResultStream().findFirst().orElse(null);
    }

    public int countByBrand(Brand brand) {
//        return entityManager.createQuery("SELECT count(ca) FROM CarAd ca " +
//                        "JOIN ca.model m WHERE m.brand = :brand", Long.class)
//                .setParameter("brand", brand)
//                .getSingleResult().intValue();
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(Long.class);
        var carAd = cq.from(CarAd.class); // FROM CarAd carAd
        cq.select(cb.count(carAd)); // SELECT count(carAd)
        cq.where(
                cb.equal(carAd.get("model").get("brand"), brand) // carAd.model.brand = xxxx
        );
        return entityManager.createQuery(cq).getSingleResult().intValue();
    }

    public List<CarAd> findByModelAndPrice(Model model, BigDecimal from, BigDecimal to) {
//        return entityManager.createQuery("SELECT ca FROM CarAd ca where ca.model = :model and ca.price between :from and :to", CarAd.class)
//                .setParameter("model", model)
//                .setParameter("from", from)
//                .setParameter("to", to)
//                .getResultList();
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(CarAd.class);
        var carAd = cq.from(CarAd.class); // FROM CarAd carAd
        cq.select(carAd); // SELECT count(carAd)
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
        cq.where(predicate).orderBy(cb.asc(carAd.get("id")));

        CarAdSearchResults results = new CarAdSearchResults();
        cq.select(cb.construct(CarAdDto.class, carAd.get("id"), brand.get("name"), model.get("name"))); // SELECT NEW CarAdDto(....)
        results.ads = entityManager.createQuery(cq).setMaxResults(query.perPage).setFirstResult((query.page - 1) * query.perPage).getResultList();
        cq.select(cb.count(carAd)).orderBy();
        results.totalCount = ((Long) entityManager.createQuery(cq).getSingleResult()).intValue();
        results.pageNumber = query.page;
        results.perPage = query.perPage;
        results.pagesCount = results.totalCount / results.perPage + (results.totalCount % results.perPage > 0 ? 1 : 0);

        return results;
    }

}
