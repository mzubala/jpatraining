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
        Root<Brand> brand = criteriaQuery.from(Brand.class);
        criteriaQuery.select(brand);
        criteriaQuery.where(criteriaBuilder.equal(brand.get("name"), name));
        return entityManager.createQuery(criteriaQuery).getResultStream().findFirst().orElse(null);
    }

    public Model model(String brandName, String modelName) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Model> criteriaQuery = criteriaBuilder.createQuery(Model.class);
        Root<Model> model = criteriaQuery.from(Model.class);
        Join<Model, Brand> brand = model.join("brand"); // JOIN model.brand
        criteriaQuery.select(model);
        criteriaQuery.where(
                criteriaBuilder.equal(brand.get("name"), brandName),
                criteriaBuilder.equal(model.get("name"), modelName)
        );
        return entityManager.createQuery(criteriaQuery).getResultStream().findFirst().orElse(null);
    }

    public int countByBrand(Brand brand) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<CarAd> carAd = criteriaQuery.from(CarAd.class);
        Join<CarAd, Model> model = carAd.join("model"); // JOIN carAd.model
        criteriaQuery.select(criteriaBuilder.count(carAd)); // SELECT count(carAd)
        criteriaQuery.where(
                criteriaBuilder.equal(model.get("brand"), brand)
        );
        return entityManager.createQuery(criteriaQuery).getSingleResult().intValue();
    }

    public List<CarAd> findByModelAndPrice(Model model, BigDecimal from, BigDecimal to) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CarAd> criteriaQuery = criteriaBuilder.createQuery(CarAd.class);
        Root<CarAd> carAd = criteriaQuery.from(CarAd.class);
        criteriaQuery.select(carAd); // SELECT count(carAd)
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
            predicate = criteriaBuilder.and(
                    predicate,
                    criteriaBuilder.equal(carAd.get("damaged"), query.damaged)
            );
        }
        if(query.fuel != null) {
            predicate = criteriaBuilder.and(
                    predicate,
                    criteriaBuilder.equal(carAd.get("fuel"), query.fuel)
            );
        }
        if(query.brand != null) {
            predicate = criteriaBuilder.and(
                    predicate,
                    criteriaBuilder.equal(brand.get("name"), query.brand)
            );
        }
        if(query.firstOwner != null) {
            predicate = criteriaBuilder.and(
                    predicate,
                    criteriaBuilder.equal(carAd.get("firstOwner"), query.firstOwner)
            );
        }

        criteriaQuery.where(predicate);
        criteriaQuery.select(criteriaBuilder.construct(CarAdDto.class, carAd.get("id"), brand.get("name"), model.get("name"))); // SELECT new CarAdDto(...)
        results.ads = entityManager.createQuery(criteriaQuery)
                .setMaxResults(query.perPage) // LIMIT
                .setFirstResult(query.perPage * (query.page - 1)) // OFFSET
                .getResultList();

        criteriaQuery.select(criteriaBuilder.count(carAd));
        results.totalCount = ((Long) entityManager.createQuery(criteriaQuery).getSingleResult()).intValue();
        results.pageNumber = query.page;
        results.perPage = query.perPage;
        results.pagesCount = results.totalCount / results.perPage + (results.totalCount % results.perPage > 0 ? 1 : 0);
        return results;
    }

}
