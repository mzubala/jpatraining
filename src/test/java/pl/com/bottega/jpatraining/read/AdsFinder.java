package pl.com.bottega.jpatraining.read;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

public class AdsFinder {

    private final EntityManager entityManager;

    public AdsFinder(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Brand brand(String name) {
        return (Brand) entityManager
            .createQuery("FROM Brand b WHERE b.name = :name")
            .setParameter("name", name).getSingleResult();
    }

    public Model model(String brandName, String modelName) {
        return (Model) entityManager
            .createQuery("SELECT model FROM Model model " +
                "JOIN model.brand brand " +
                "WHERE model.name = :modelName " +
                "AND brand.name = :brandName")
            .setParameter("modelName", modelName)
            .setParameter("brandName", brandName)
            .getSingleResult();
    }

    public int countByBrand(Brand brand) {
        return ((Long) entityManager.createQuery("" +
            "SELECT count(ad) " +
            "FROM CarAd ad JOIN ad.model model " +
            "WHERE model.brand = :brand")
            .setParameter("brand", brand)
            .getSingleResult())
            .intValue();
    }

    public List<CarAd> findByModelAndPrice(Model model, BigDecimal from, BigDecimal to) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<CarAd> cq =  cb.createQuery(CarAd.class);
        Root<CarAd> carAddRoot = cq.from(CarAd.class);
        cq.where(
            cb.and(
                cb.between(carAddRoot.get("price"), from, to),
                cb.equal(carAddRoot.get("model"), model)
            )
        );

        return entityManager.createQuery(cq).getResultList();
    }

    public CarAdSearchResults search(CarAdQuery query) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery cq =  cb.createQuery(CarAdDto.class);
        Root<CarAd> root = cq.from(CarAd.class);
        Join<CarAd, Model> model = root.join("model");
        Join<Model, Brand> brand = model.join("brand");
        cq.select(cb.construct(CarAdDto.class, root.get("id"), brand.get("name"), model.get("name")));
        Predicate predicate = cb.conjunction();
        if(query.brand != null) {
            predicate = cb.and(predicate, cb.equal(brand.get("name"), query.brand));
        }
        if(query.damaged != null) {
            predicate = cb.and(predicate, cb.equal(root.get("damaged"), query.damaged));
        }
        if(query.firstOwner != null) {
            predicate = cb.and(predicate, cb.equal(root.get("firstOwner"), query.firstOwner));
        }
        if(query.fuel != null) {
            predicate = cb.and(predicate, cb.equal(root.get("fuel"), query.fuel));
        }
        cq.where(predicate);
        List<CarAdDto> dtos = entityManager.createQuery(cq)
            .setFirstResult((query.page - 1) * query.perPage)
            .setMaxResults(query.perPage)
            .getResultList();
        CarAdSearchResults results = new CarAdSearchResults();
        results.ads = dtos;
        results.perPage = query.perPage;
        results.pageNumber = query.page;
        cq.select(cb.count(root));
        Long totalCount = (Long) entityManager.createQuery(cq).getSingleResult();
        results.totalCount = totalCount.intValue();
        results.pagesCount = results.totalCount / results.perPage +
            (results.totalCount % results.perPage > 0 ? 1 : 0) ;
        return results;
    }

}
