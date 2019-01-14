package pl.com.bottega.jpatraining.read;



import org.hibernate.Session;

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
        Query query = entityManager.createQuery("SELECT COUNT(ca) FROM CarAd ca join ca.model m where m.brand=:brand");
        query.setParameter("brand", brand);
        return (int) (long) query.getSingleResult();
    }

    public List<CarAd> findByModelAndPrice(Model model, BigDecimal from, BigDecimal to) {
        /*Query query = entityManager.createQuery("SELECT ca FROM CarAd ca where ca.model=:model and ca.price between :from and :to");
        query.setParameter("model", model);
        query.setParameter("from", from);
        query.setParameter("to", to);*/
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(CarAd.class);
        criteriaQuery.select(root);
        criteriaQuery.where(
            criteriaBuilder.equal(root.get("model"), model),
            criteriaBuilder.between(root.get("price"), from, to)
        );
        Query query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();

    }

    public CarAdSearchResults search(CarAdQuery query) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = cb.createQuery();
        Root root = criteriaQuery.from(CarAd.class);
        Join model = root.join("model");
        Join brand = model.join("brand");
        criteriaQuery.select(cb.construct(CarAdDto.class, root.get("id"), brand.get("name"), model.get("name")));

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
        if(query.damaged != null) {
            predicate = cb.and(predicate, cb.equal(root.get("damaged"), query.damaged));
        }
        if(query.firstOwner != null) {
            predicate = cb.and(predicate, cb.equal(root.get("firstOwner"), query.firstOwner));
        }

        criteriaQuery.where(predicate);
        Query q = entityManager.createQuery(criteriaQuery);
        q.setMaxResults(query.perPage);
        q.setFirstResult((query.page - 1) * query.perPage);
        CarAdSearchResults results = new CarAdSearchResults();
        results.ads = q.getResultList();
        criteriaQuery.select(cb.count(root));
        results.totalCount = (int) (long) entityManager.createQuery(criteriaQuery).getSingleResult();
        results.pagesCount = results.totalCount / query.perPage + (results.totalCount % query.perPage > 0 ? 1 : 0);

        results.pageNumber = query.page;
        results.perPage = query.perPage;
        return results;
    }

}
