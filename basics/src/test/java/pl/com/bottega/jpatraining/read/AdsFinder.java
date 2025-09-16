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

    // zwraca Brand o zadanej nazwie lub null jezeli nie ma takiego
    public Brand brand(String name) {
        /*return entityManager.createQuery("SELECT b FROM Brand b WHERE b.name = :name", Brand.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst()
                .orElse(null);*/
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(Brand.class);
        var brand = cq.from(Brand.class);
        cq.select(brand);
        cq.where(cb.equal(brand.get("name"), name));
        return entityManager.createQuery(cq)
            .getResultStream()
            .findFirst()
            .orElse(null);
    }

    // zwraca Model o zadanej nazwie i nazwie marki lub null jezeli nie ma takiego
    public Model model(String brandName, String modelName) {
        //return entityManager.createQuery("SELECT m FROM Model m WHERE m.name = :modelName AND m.brand.name = :brandName", Model.class)
        /*return entityManager.createQuery("SELECT m FROM Model m JOIN m.brand b WHERE m.name = :modelName AND b.name = :brandName", Model.class)
                .setParameter("modelName", modelName)
                .setParameter("brandName", brandName)
                .getResultStream()
                .findFirst()
                .orElse(null);*/
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(Model.class);
        var model = cq.from(Model.class);
        cq.where(
            cb.equal(model.get("name"), modelName),
            cb.equal(model.get("brand").get("name"), brandName)
        );
        return entityManager.createQuery(cq)
            .getResultStream()
            .findFirst()
            .orElse(null);
    }

    // zwraca liczbę ogłoszeń sprzedaży danej marki
    public int countByBrand(Brand brand) {
        /*return entityManager.createQuery("SELECT COUNT(a) FROM CarAd a WHERE a.model.brand = :brand", Long.class)
                .setParameter("brand", brand)
                .getSingleResult()
                .intValue();*/
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(Long.class);
        var ad = cq.from(CarAd.class);
        cq.select(cb.count(ad));
        var model = ad.join("model");
        cq.where(cb.equal(model.get("brand"), brand));
        return entityManager.createQuery(cq)
            .getSingleResult().intValue();
    }

    // zwraca ogłoszenia sprzedaży danego modelu i w podanym przedziale cenowym
    public List<CarAd> findByModelAndPrice(Model model, BigDecimal from, BigDecimal to) {
        /*return entityManager.createQuery("SELECT a FROM CarAd a WHERE a.model = :model AND a.price BETWEEN :from AND :to", CarAd.class)
                .setParameter("model", model)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();*/
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(CarAd.class);
        var ad = cq.from(CarAd.class);
        cq.select(ad);
        cq.where(
            cb.equal(ad.get("model"), model),
            cb.between(ad.get("price"), from, to)
        );
        return entityManager.createQuery(cq).getResultList();
    }

    public CarAdSearchResults search(CarAdQuery query) {
        var cb = entityManager.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        var ad = cq.from(CarAd.class);
        var predicate = cb.conjunction();
        if(query.brand != null) {
            predicate = cb.and(predicate, cb.equal(ad.get("model").get("brand").get("name"), query.brand));
        }
        if(query.damaged != null) {
            predicate = cb.and(predicate, cb.equal(ad.get("damaged"), query.damaged));
        }
        if(query.firstOwner != null) {
            predicate = cb.and(predicate, cb.equal(ad.get("firstOwner"), query.firstOwner));
        }
        if(query.fuel != null) {
            predicate = cb.and(predicate, cb.equal(ad.get("fuel"), query.fuel));
        }
        cq.where(predicate);
        cq.select(cb.count(ad));
        var results = new CarAdSearchResults();
        results.totalCount = ((Long) entityManager.createQuery(cq).getSingleResult()).intValue();
        results.pageNumber = query.page;
        results.perPage = query.perPage;
        results.pagesCount = results.totalCount / results.perPage + (results.totalCount % results.perPage == 0 ? 0 :1 );
        //results.pagesCount = (int) Math.ceil((double) results.totalCount / results.perPage);
        cq.select(cb.construct(CarAdDto.class, ad.get("id"), ad.get("model").get("brand").get("name"), ad.get("model").get("name")));
        cq.orderBy(cb.asc(ad.get("id")));
        results.ads = entityManager.createQuery(cq)
            .setMaxResults(query.perPage) // LIMIT
            .setFirstResult((query.page - 1) * query.perPage) // OFFSET
            .getResultList();
        return results;
    }

}
