package pl.com.bottega.jpatraining.read;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.List;

public class AdsFinder {

    private final EntityManager entityManager;

    public AdsFinder(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Brand brand(String name) {
        /*return entityManager.createQuery("Select b FROM Brand b WHERE b.name = :name", Brand.class)
            .setParameter("name", name)
            .getResultStream()
            .findFirst().orElse(null);*/

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Brand> cq = cb.createQuery(Brand.class);
        Root<Brand> brand = cq.from(Brand.class);
        cq.select(brand);
        cq.where(cb.equal(brand.get("name"), name));
        return entityManager.createQuery(cq)
            .getResultStream()
            .findFirst().orElse(null);
    }

    public Model model(String brandName, String modelName) {
        /*return entityManager.createQuery("Select m FROM Model m join m.brand b WHERE m.name=:modelName and b.name=:brandName", Model.class)
            .setParameter("modelName", modelName)
            .setParameter("brandName", brandName)
            .getResultStream()
            .findFirst().orElse(null);*/
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Model> cq = cb.createQuery(Model.class);
        Root<Model> model = cq.from(Model.class);
        Join<Model, Brand> brand = model.join("brand");
        cq.select(model);
        cq.where(cb.equal(brand.get("name"), brandName), cb.equal(model.get("name"), modelName));
        return entityManager.createQuery(cq)
            .getResultStream()
            .findFirst().orElse(null);
    }

    public int countAdsByBrand(Brand brand) {
        /*return entityManager.createQuery("SELECT COUNT(ca) FROM CarAd ca join ca.model m where m.brand = :brand", Long.class)
            .setParameter("brand", brand)
            .getSingleResult().intValue();*/
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<CarAd> carAd = cq.from(CarAd.class);
        Join<CarAd, Model> model = carAd.join("model");
        cq.select(cb.count(carAd));
        cq.where(cb.equal(model.get("brand"), brand));
        return entityManager.createQuery(cq)
            .getSingleResult().intValue();
    }

    public List<CarAd> findByModelAndPrice(Model model, BigDecimal from, BigDecimal to) {
        /*return entityManager.createQuery("SELECT ca FROM CarAd ca where ca.model = :model and ca.price between :from and :to", CarAd.class)
            .setParameter("model", model)
            .setParameter("from", from)
            .setParameter("to", to)
            .getResultList();*/
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<CarAd> cq = cb.createQuery(CarAd.class);
        Root<CarAd> carAd = cq.from(CarAd.class);
        cq.select(carAd);
        cq.where(cb.equal(carAd.get("model"), model), cb.between(carAd.get("price"), from, to));
        return entityManager.createQuery(cq)
            .getResultList();
    }

    public CarAdSearchResults search(CarAdQuery query) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<CarAd> carAd = cq.from(CarAd.class);
        Join<CarAd, Model> model = carAd.join("model");
        Join<Model, Brand> brand = carAd.join("brand");
        Predicate predicate = cb.conjunction();
        if(query.brand != null) {
            predicate = cb.and(predicate, cb.equal(brand.get("name"), query.brand));
        }
        if(query.fuel != null) {
            //TODO
        }
        // TODO - ify dla pozostałych
        cq.where(predicate);

        cq.select(cb.count(carAd));
        // TODO - execute query and get count

        cq.select(cb.construct(CarAdDto.class, carAd.get("id"), brand.get("name"), model.get("name")));
        // TODO - execute and get dtos (remember about offset and limit)

        return null; // TODO create and return CarAdSearchResults
    }

}
