package pl.com.bottega.jpatraining.locking;

import org.junit.Test;
import pl.com.bottega.jpatraining.BaseJpaTest;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
    public void buysInventoryInConcurrentEnvironment() {
        // given
        initialInventory();
        AtomicInteger errorsCount = new AtomicInteger();
        Runnable buyer = () -> {
            while (template.getEntityManager().find(Inventory.class, skuCode).getCount() > 0) {
                try {
                    template.executeInTx((em) -> {
                        createInventoryUpdater().buy(skuCode, 4);
                    });
                } catch (RuntimeException ex) {
                    //System.out.println("========= Ups!!!!! =========");
                    errorsCount.incrementAndGet();
                }
                template.close();
            }
        };
        List<Thread> threads = new LinkedList<>();

        // when
        for (int i = 0; i < 4; i++) {
            Thread t = new Thread(buyer);
            threads.add(t);
            t.start();
        }
        threads.forEach((t) -> {
            try {
                t.join(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        // then
        assertThat(txAmounts()).isEqualTo(-1000);
        System.out.println("Errors count = " + errorsCount.get());
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

        //return new PesimisticInventoryUpdater(template.getEntityManager());
        return new OptimisticInventoryUpdater(template.getEntityManager());
    }

    private Long txAmounts() {
        return (Long) template.getEntityManager()
            .createQuery("SELECT sum(tx.txAmount) FROM InventoryTx tx")
            .getSingleResult();
    }

}
