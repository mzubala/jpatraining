package pl.com.bottega.jpatraining.read;

import javax.persistence.EntityManager;
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
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Brand> criteriaQuery = criteriaBuilder.createQuery(Brand.class);
        Root<Brand> brand = criteriaQuery.from(Brand.class); // FROM Brand b
        criteriaQuery.select(brand); // SELECT b
        criteriaQuery.where(criteriaBuilder.equal(brand.get("name"), criteriaBuilder.parameter(String.class, "name"))); // b.name = :name
        return entityManager.createQuery(criteriaQuery)
                .setParameter("name", name)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    public Model model(String brandName, String modelName) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Model> criteriaQuery = criteriaBuilder.createQuery(Model.class);
        Root<Model> model = criteriaQuery.from(Model.class); // FROM Model m
        Join<Model, Brand> brand = model.join("brand"); // JOIN m.brand b
        criteriaQuery.select(model); // SELECT model
        criteriaQuery.where(
                criteriaBuilder.equal(brand.get("name"), criteriaBuilder.parameter(String.class, "brandName")),
                criteriaBuilder.equal(model.get("name"), criteriaBuilder.parameter(String.class, "modelName"))
        ); // b.name = :name
        return entityManager.createQuery(criteriaQuery)
                .setParameter("modelName", modelName)
                .setParameter("brandName", brandName)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    public int countByBrand(Brand brand) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<CarAd> carAd = criteriaQuery.from(CarAd.class);
        Join<CarAd, Model> model = carAd.join("model");
        criteriaQuery.select(criteriaBuilder.count(carAd)); // SELECT count(b)
        criteriaQuery.where(criteriaBuilder.equal(model.get("brand"), brand)); // m.brand = brand
        return entityManager.createQuery(criteriaQuery).getSingleResult().intValue();
    }

    public List<CarAd> findByModelAndPrice(Model model, BigDecimal from, BigDecimal to) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CarAd> criteriaQuery = criteriaBuilder.createQuery(CarAd.class);
        Root<CarAd> carAd = criteriaQuery.from(CarAd.class); // FROM CarAd ad
        criteriaQuery.select(carAd); // SELECT ad
        criteriaQuery.where(
                criteriaBuilder.equal(carAd.get("model"), model),
                criteriaBuilder.between(carAd.get("price"), from, to)
        );
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public CarAdSearchResults search(CarAdQuery query) {
        CarAdSearchResults results = new CarAdSearchResults();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root<CarAd> carAd = criteriaQuery.from(CarAd.class);
        Join<CarAd, Model> model = carAd.join("model");
        Join<Model, Brand> brand = model.join("brand");
        Predicate predicate = criteriaBuilder.and();
        if(query.damaged != null) {
            // predicate = ...
        }
        if(query.fuel != null) {
            // predicate = ...
        }
        if(query.brand != null) {
            predicate = criteriaBuilder.and(
                    predicate,
                    criteriaBuilder.equal(brand.get("name"), query.brand)
            );
        }
        if(query.firstOwner != null) {
            // predicate = ...
        }

        criteriaQuery.where(predicate);
        criteriaQuery.select(criteriaBuilder.construct(CarAdDto.class, carAd.get("id"), brand.get("name"), model.get("name")));
        results.ads = entityManager.createQuery(criteriaQuery)
                //.setMaxResults()
                //.setFirstResult()
                .getResultList();

        //criteriaQuery.select(criteriaBuilder.count(...))
        results.totalCount = ((Long) entityManager.createQuery(criteriaQuery).getSingleResult()).intValue();
        results.pageNumber = query.page;
        results.perPage = query.perPage;
        //results.pagesCount = ...
        return results;
    }

}
