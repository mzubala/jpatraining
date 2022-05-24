package pl.com.bottega.jpatraining.associations;

import org.junit.jupiter.api.Test;
import pl.com.bottega.jpatraining.BaseJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class AssociationsTest extends BaseJpaTest {

    @Test
    public void createsOrder() {
        // given
        template.getStatistics().clear();
        Product p1 = savedProduct();
        Product p2 = savedProduct();

        // when
        Order order = new Order();
        order.getItems().add(new LineItem(order, p1, 10));
        order.getItems().add(new LineItem(order, p2, 20));
        template.executeInTx((em) -> {
            em.persist(order);
        });
        template.close();

        // then
        //assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(3);
        template.executeInTx((em) -> {
            Order fetched = em.find(Order.class, order.getId());
            assertThat(fetched).isNotNull();
            assertThat(fetched.getItems().size()).isEqualTo(2);
            LineItem i1 = fetched.getItems().get(0);
            LineItem i2 = fetched.getItems().get(1);
            assertThat(i1.getCount()).isEqualTo(10);
            assertThat(i2.getCount()).isEqualTo(20);
            assertThat(i1.getProduct()).isNotNull();
            assertThat(i2.getProduct()).isNotNull();
        });
    }

    @Test
    public void addsProductToOrderWith1Query() {
        // given
        Product p1 = savedProduct();
        Product p2 = savedProduct();
        Product p3 = savedProduct();
        Order order = new Order();
        order.getItems().add(new LineItem(order, p1, 10));
        order.getItems().add(new LineItem(order, p2, 20));
        template.executeInTx((em) -> {
            em.persist(order);
        });
        template.close();
        template.getStatistics().clear();

        // when
        template.executeInTx((em) -> {
            AddProductToOrder addProductToOrder = new AddProductToOrder(em);
            addProductToOrder.add(order.getId(), p3.getId(), 100);
        });

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1);
        template.close();
        template.executeInTx((em) -> {
            Order fetched = em.find(Order.class, order.getId());
            assertThat(fetched).isNotNull();
            assertThat(fetched.getItems().size()).isEqualTo(3);
        });
    }

    private Product savedProduct() {
        return template.executeInTx((em) -> {
            Product p = newProduct();
            em.persist(p);
            return p;
        });
    }

    private Product newProduct() {
        Product product = new Product();
        product.setName("test");
        product.setPrice(new BigDecimal(100.0));
        return product;
    }

}
