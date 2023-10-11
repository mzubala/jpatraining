package pl.com.bottega.jpatraining.read;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.JoinType;

import java.math.BigDecimal;
import java.util.List;

public class AdsFinder {

    private final EntityManager entityManager;

    public AdsFinder(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Brand brand(String name) {
        /**return entityManager.createQuery("SELECT b FROM Brand b WHERE b.name = :brandName", Brand.class)
         .setParameter("brandName", name).getResultStream().findFirst().orElse(null);*/
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(Brand.class);
        var brand = cq.from(Brand.class); // FROM Brand brand
        cq.select(brand); // SELECT brand
        cq.where(cb.equal(brand.get("name"), name)); // brand.name = xxxx
        return entityManager.createQuery(cq).getResultStream().findFirst().orElse(null);
    }

    public Model model(String brandName, String modelName) {
        /*return entityManager.createQuery("SELECT m FROM Model m "
                        + "WHERE m.brand.name = :brandName AND m.name = :modelName", Model.class)
                .setParameter("brandName", brandName)
                .setParameter("modelName", modelName)
                .getResultStream().findFirst().orElse(null);*/
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
        /*return entityManager.createQuery("SELECT count(ca) FROM CarAd ca " +
                        "JOIN ca.model m WHERE m.brand = :brand", Long.class)
                .setParameter("brand", brand)
                .getSingleResult().intValue();*/
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
        /*return entityManager.createQuery("SELECT ca FROM CarAd ca where ca.model = :model and ca.price between :from and :to", CarAd.class)
                .setParameter("model", model)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();*/
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
        var cq = cb.createQuery();
        var predicate = cb.conjunction();
        var carAd = cq.from(CarAd.class);
        if (query.brand != null) {
            predicate = cb.and(
                    predicate,
                    cb.equal(carAd.get("model").get("brand").get("name"), query.brand)
            );
        }
        if (query.fuel != null) {
            // TODO
        }
        if(query.firstOwner != null) {
            // TODO
        }
        if(query.damaged != null) {
            // TODO
        }
        cq.where(predicate);
        cq.orderBy(cb.asc(carAd.get("id")));
        cq.multiselect(cb.construct(CarAdDto.class, carAd.get(""), carAd.get(""), carAd.get(""))); // TODO
        var result = new CarAdSearchResults();
        result.ads = entityManager.createQuery(cq)
                .setFirstResult(0) // TODO
                .setMaxResults(0) // TODO
                .getResultStream()
                .map(o -> (CarAdDto) o)
                .toList();
        cq.multiselect(cb.count(carAd));
        cq.orderBy(List.of());
        result.totalCount = ((Long) entityManager.createQuery(cq).getSingleResult()).intValue();
        result.pageNumber = query.page;
        result.perPage = query.perPage;
        result.pagesCount = (result.totalCount + result.perPage - 1)/result.perPage;
        //result.pagesCount = result.totalCount / result.perPage + (result.totalCount % result.perPage == 0 ? 0 : 1);
        return result;
    }
}
