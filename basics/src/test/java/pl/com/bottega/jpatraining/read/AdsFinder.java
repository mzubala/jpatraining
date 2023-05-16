package pl.com.bottega.jpatraining.read;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CompoundSelection;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.math.BigDecimal;
import java.util.List;

public class AdsFinder {

    private final EntityManager entityManager;

    public AdsFinder(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Brand brand(String name) {
        return entityManager.createQuery("SELECT b " + "FROM Brand b " + "WHERE b.name = :brandName", Brand.class)
            .setParameter("brandName", name).getResultStream().findFirst().orElse(null);
    }

    public Model model(String brandName, String modelName) {
        return entityManager.createQuery("SELECT m " + "FROM Model m " + "JOIN FETCH m.brand b "
                + "WHERE b.name = :brandName AND m.name = :modelName", Model.class).setParameter("brandName", brandName)
            .setParameter("modelName", modelName).getResultStream().findFirst().orElse(null);
    }

    public int countByBrand(Brand brand) {
        return 0;
    }

    public List<CarAd> findByModelAndPrice(Model model, BigDecimal from, BigDecimal to) {
        return null;
    }

    public CarAdSearchResults search(CarAdQuery query) {
        var cb = entityManager.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        var carAd = cq.from(CarAd.class);
        var model = carAd.join("model");
        var brand = model.join("brand");
        cq.multiselect(constructCarAdDto(cb, carAd, model, brand));
        cq.where(createPredicate(query, cb, carAd, brand, cb.conjunction()));
        cq.orderBy(createOrder(cb, carAd));
        var result = new CarAdSearchResults();
        result.ads = getCarAdDtos(query, cq);
        result.perPage = query.perPage;
        result.pageNumber = query.page;
        cq.orderBy(List.of());
        cq.multiselect(cb.count(carAd));
        result.totalCount = getTotalCount(cq);
        result.pagesCount = calculatePagesCount(result);
        return result;
    }

    private static int calculatePagesCount(CarAdSearchResults result) {
        return result.totalCount / result.perPage + (result.totalCount % result.perPage != 0 ? 1 : 0);
    }

    private int getTotalCount(CriteriaQuery cq) {
        return ((Long) entityManager.createQuery(cq).getSingleResult()).intValue();
    }

    private List getCarAdDtos(CarAdQuery query, CriteriaQuery cq) {
        return entityManager.createQuery(cq).setFirstResult(query.perPage * (query.page - 1))
            .setMaxResults(query.perPage).getResultList();
    }

    private static Order createOrder(CriteriaBuilder cb, Root carAd) {
        return cb.asc(carAd.get("id"));
    }

    private static CompoundSelection<CarAdDto> constructCarAdDto(CriteriaBuilder cb, Root carAd, Join model, Join brand) {
        return cb.construct(CarAdDto.class, carAd.get("id"), brand.get("name"), model.get("name"));
    }

    private static Predicate createPredicate(
        CarAdQuery query, CriteriaBuilder cb, Root carAd, Join brand, Predicate predicate
    ) {
        predicate = addBrandPredicate(query, cb, brand, predicate);
        predicate = addDamagedPredicate(query, cb, carAd, predicate);
        predicate = addFirstOwnerPredicate(query, cb, carAd, predicate);
        predicate = addFuelPredicate(query, cb, carAd, predicate);
        return predicate;
    }

    private static Predicate addFuelPredicate(CarAdQuery query, CriteriaBuilder cb, Root carAd, Predicate predicate) {
        if (query.fuel != null) {
            predicate = cb.and(predicate, cb.equal(carAd.get("fuel"), query.fuel));
        }
        return predicate;
    }

    private static Predicate addFirstOwnerPredicate(CarAdQuery query, CriteriaBuilder cb, Root carAd, Predicate predicate) {
        if (query.firstOwner != null) {
            predicate = cb.and(predicate, cb.equal(carAd.get("firstOwner"), query.firstOwner));
        }
        return predicate;
    }

    private static Predicate addDamagedPredicate(CarAdQuery query, CriteriaBuilder cb, Root carAd, Predicate predicate) {
        if (query.damaged != null) {
            predicate = cb.and(predicate, cb.equal(carAd.get("damaged"), query.damaged));
        }
        return predicate;
    }

    private static Predicate addBrandPredicate(CarAdQuery query, CriteriaBuilder cb, Join brand, Predicate predicate) {
        if (query.brand != null) {
            predicate = cb.and(predicate, cb.equal(brand.get("name"), query.brand));
        }
        return predicate;
    }

}
