package pl.com.bottega.jpatraining;

import jakarta.persistence.metamodel.Metamodel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.EntityType;
import java.util.function.Consumer;
import java.util.function.Function;

public class EntityManagerTemplate {

    private static EntityManagerFactory emf;

    private ThreadLocal<EntityManager> entityManagerThreadLocal = new ThreadLocal<>();
    private String unitName = "jpatraining";

    public EntityManager createEntityManager() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory(unitName);
        }
        getStatistics().setStatisticsEnabled(true);
        return emf.createEntityManager();
    }

    public Statistics getStatistics() {
        return emf.unwrap(SessionFactory.class).getStatistics();
    }

    public void close() {
        getEntityManager().close();
        entityManagerThreadLocal.remove();
    }

    public EntityManager getEntityManager() {
        EntityManager em = entityManagerThreadLocal.get();
        if (em == null) {
            em = createEntityManager();
            entityManagerThreadLocal.set(em);
        }
        return em;
    }

    public <T> T executeInTx(Function<EntityManager, T> fun) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {
            T result = fun.apply(em);
            tx.commit();
            return result;
        } catch (RuntimeException ex) {
            tx.rollback();
            throw ex;
        }
    }

    public void cleanDb() {
        executeInTx((em) -> {
            Session session = em.unwrap(Session.class);
            Metamodel hibernateMetadata = session.getSessionFactory().getMetamodel();
            session.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
            hibernateMetadata.getEntities().stream()
                .map(this::getTableName)
                .forEach(tableName -> session.createNativeQuery("TRUNCATE TABLE \"" + tableName + "\"")
                    .executeUpdate()
                );
            session.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
        });
    }

    private String getTableName(EntityType<?> entityType) {
        Table table = entityType.getJavaType().getAnnotation(Table.class);
        return table == null ? entityType.getName() : table.name();
    }

    public void executeInTx(Consumer<EntityManager> consumer) {
        executeInTx((em) -> {
            consumer.accept(em);
            return null;
        });
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
}
