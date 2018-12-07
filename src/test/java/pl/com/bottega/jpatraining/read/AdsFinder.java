package pl.com.bottega.jpatraining.read;


import javax.persistence.EntityManager;
import javax.persistence.Query;
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

    public Brand brand(String name) {
        Query query = entityManager.createQuery("Select b FROM Brand b WHERE b.name = :name");
        query.setParameter("name", name);
        return (Brand) query.getSingleResult();
    }

    public Model model(String brandName, String modelName) {
        Query query = entityManager.createQuery("Select m FROM Model m join m.brand b WHERE m.name=:modelName and b.name=:brandName");
        query.setParameter("modelName", modelName);
        query.setParameter("brandName", brandName);
        return (Model) query.getSingleResult();
    }

    public int countByBrand(Brand brand) {
        Query query = entityManager.createQuery("SELECT COUNT(ca) FROM CarAd ca join ca.model m join m.brand b where b=:brand");
        query.setParameter("brand", brand);
        return (int) (long) query.getSingleResult();
    }

    public List<CarAd> findByModelAndPrice(Model model, BigDecimal from, BigDecimal to) {
        Query query = entityManager.createQuery("SELECT ca FROM CarAd ca join ca.model m where m=:model and ca.price between :from and :to");
        query.setParameter("model", model);
        query.setParameter("from", from);
        query.setParameter("to", to);
        return query.getResultList();

    }

    public CarAdSearchResults search(CarAdQuery query) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root root = cq.from(CarAd.class);
        Join model = root.join("model");
        Join brand = model.join("brand");
        Predicate predicate = cb.conjunction();
        if (query.fuel != null) {
            predicate = cb.and(predicate, cb.equal(root.get("fuel"), query.fuel));
        }
        if (query.brand != null) {
            predicate = cb.and(predicate, cb.equal(brand.get("name"), query.brand));
        }
        if (query.model != null) {
            predicate = cb.and(predicate, cb.equal(model.get("name"), query.model));
        }
        predicate = cb.and(predicate, cb.equal(root.get("damaged"), query.damaged));
        predicate = cb.and(predicate, cb.equal(root.get("firstOwner"), query.firstOwner));
        cq.select(cb.construct(CarAdDto.class,
            root.get("id"),
            brand.get("name"),
            model.get("name")
        ));
        cq.where(predicate);
        Query emq = entityManager.createQuery(cq);
        CarAdSearchResults results = new CarAdSearchResults();
        results.ads = emq.getResultList();
        return results;
    }

}
