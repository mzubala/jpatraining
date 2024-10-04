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
        /*return entityManager.createQuery("SELECT b FROM Brand b WHERE name = :name", Brand.class)
            .setParameter("name", name)
            .getResultStream()
            .findFirst()
            .orElse(null);*/
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(Brand.class);
        var brand = cq.from(Brand.class); // FROM Brand brand
        cq.select(brand); // SELECT brand
        cq.where(cb.equal(brand.get("name"), name)); // WHERE brand.name = 'name'
        return entityManager.createQuery(cq)
            .getResultStream()
            .findFirst()
            .orElse(null);
    }

    public Model model(String brandName, String modelName) {
        /*return entityManager
            .createQuery("SELECT m FROM Model m JOIN m.brand b WHERE b.name = :brandName AND m.name = :modelName", Model.class)
            //.createQuery("SELECT m FROM Model m WHERE m.brand.name = :brandName AND m.name = :modelName", Model.class)
            .setParameter("brandName", brandName)
            .setParameter("modelName", modelName)
            .getResultStream()
            .findFirst()
            .orElse(null);*/
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(Model.class);
        var model = cq.from(Model.class); // FROM Model model
        var brand = model.join("brand");
        cq.select(model); // SELECT model
        cq.where(
            cb.equal(model.get("name"), modelName),
            cb.equal(brand.get("name"), brandName)
        ); // WHERE brand.name = '...' AND model.name = '...'
        return entityManager.createQuery(cq)
            .getResultStream()
            .findFirst()
            .orElse(null);
    }

    public int countByBrand(Brand brand) {
        /*return entityManager.createQuery("SELECT count(add) FROM CarAd add WHERE add.model.brand = :brand", Long.class)
            .setParameter("brand", brand).getSingleResult().intValue();*/
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(Long.class);
        var carAdd = cq.from(CarAd.class);
        cq.select(cb.count(carAdd));
        cq.where(cb.equal(carAdd.get("model").get("brand"), brand));
        return entityManager.createQuery(cq).getSingleResult().intValue();
    }

    public List<CarAd> findByModelAndPrice(Model model, BigDecimal from, BigDecimal to) {
        /*return entityManager.createQuery("SELECT add FROM CarAd add WHERE add.model = :model AND add.price between :from and :to", CarAd.class)
            .setParameter("model", model)
            .setParameter("from", from)
            .setParameter("to", to)
            .getResultList();*/
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(CarAd.class);
        var carAdd = cq.from(CarAd.class);
        cq.select(carAdd).where(
            cb.equal(carAdd.get("model"), model),
            cb.between(carAdd.get("price"), from, to)
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
