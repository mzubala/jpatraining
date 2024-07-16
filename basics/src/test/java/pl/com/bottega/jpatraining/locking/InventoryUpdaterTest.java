package pl.com.bottega.jpatraining.locking;

import jakarta.persistence.RollbackException;
import org.junit.jupiter.api.Test;
import pl.com.bottega.jpatraining.BaseJpaTest;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

public class InventoryUpdaterTest extends BaseJpaTest {

    @Test
    public void buysInventory() {
        // given
        initialInventory();

        // when
        template.executeInTx(em -> {
            createInventoryUpdater().buy(skuCode, 500);
        });

        // then
        assertThat(template.getEntityManager().find(Inventory.class, skuCode).getCount()).isEqualTo(500);
        assertThat(txAmounts()).isEqualTo(-500);
    }

    @Test
    public void fillsInventory() {
        // given
        initialInventory();

        // when
        template.executeInTx(em -> {
            createInventoryUpdater().fill(skuCode, 500);
        });

        // then
        assertThat(template.getEntityManager().find(Inventory.class, skuCode).getCount()).isEqualTo(1500);
        assertThat(txAmounts()).isEqualTo(500);
    }

    @Test
    public void buysInventoryInConcurrentEnvironment() throws InterruptedException {
        // given
        initialInventory();
        var errorCounter = new AtomicInteger();
        Runnable buyer = () -> {
            while (template.getEntityManager().find(Inventory.class, skuCode).getCount() > 0) {
                System.out.println(Thread.currentThread().getName());
                try {
                    template.executeInTx((em) -> {
                        createInventoryUpdater().buy(skuCode, 4);
                    });
                } catch (RollbackException e) {
                    errorCounter.incrementAndGet();
                    System.out.println("Optimistic lock failure");
                }
                template.close();
            }
        };

        // when
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 4; i++) {
            executorService.submit(buyer);
        }
        executorService.shutdown();
        executorService.awaitTermination(5, SECONDS);

        // then
        assertThat(txAmounts()).isEqualTo(-1000);
        System.out.println("Errors count = " + errorCounter.get());
    }

    private final String skuCode = "test";
    private final Integer cout = 1000;

    private void initialInventory() {
        template.executeInTx((em) -> {
            em.persist(new Inventory(skuCode, cout));
        });
        template.close();
    }

    private InventoryUpdater createInventoryUpdater() {
        return new OptimisticInventoryUpdater(template.getEntityManager());
    }

    private Long txAmounts() {
        return (Long) template.getEntityManager()
            .createQuery("SELECT sum(tx.txAmount) FROM InventoryTx tx")
            .getSingleResult();
    }

}
