package pl.com.bottega.jpatraining.complexpk;

import org.h2.jdbc.JdbcSQLException;
import org.junit.Test;
import pl.com.bottega.jpatraining.BaseJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ComplexPkTest extends BaseJpaTest {

    @Test
    public void createsEntitiesWithComplexPk() {
        // given
        Payment payment = new Payment();
        PaymentId id = new PaymentId();
        id.setOrderId(1L);
        id.setTransactionId("tx-1");
        payment.setId(id);

        // when
        template.executeInTx((em) -> {
            em.persist(payment);
        });
        template.close();

        // then
        assertThat(template.getEntityManager().find(Payment.class, id)).isNotNull();
    }

    @Test
    public void detectsConflicts() {
        // given
        Payment payment1 = new Payment();
        PaymentId id = new PaymentId();
        id.setOrderId(1L);
        id.setTransactionId("tx-1");
        payment1.setId(id);
        Payment payment2 = new Payment();
        payment2.setId(id);

        // when
        template.executeInTx((em) -> {
            em.persist(payment1);
        });
        template.close();

        // then
        assertThatThrownBy(() -> template.executeInTx((em) -> {
            em.persist(payment2);
        })).hasRootCauseInstanceOf(JdbcSQLException.class);
    }

}
