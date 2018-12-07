package pl.com.bottega.jpatraining.inheritance;

import org.junit.Test;
import pl.com.bottega.jpatraining.BaseJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

public class UserFactoryTest extends BaseJpaTest {

    private UserFactory userFactory = new UserFactory();

    @Test
    public void adminShouldHaveInvoiceCorrectorRole() {
        User admin = userFactory.adminUser("test", "test");

        InvoiceCorrectorRole invoiceCorrector = admin.getRole(InvoiceCorrectorRole.class);

        assertThat(invoiceCorrector).isNotNull();
    }

    @Test
    public void adminShouldHaveOrderCorrectorRole() {
        User admin = userFactory.adminUser("test", "test");

        OrderCorrectorRole orderCorrectorRole = admin.getRole(OrderCorrectorRole.class);

        assertThat(orderCorrectorRole).isNotNull();
    }

    @Test
    public void supervisorShouldHaveInvoiceCorrectorRole() {
        User supervisor = userFactory.supervisorUser("test", "test");

        InvoiceCorrectorRole invoiceCorrector = supervisor.getRole(InvoiceCorrectorRole.class);

        assertThat(invoiceCorrector).isNotNull();
    }


    @Test(expected = RuntimeException.class)
    public void standardUserShouldntHaveInvoiceCorrectorRole() {
        User standardUser = userFactory.standardUser("test", "test");

        standardUser.getRole(InvoiceCorrectorRole.class);
    }

    @Test(expected = RuntimeException.class)
    public void standardUserShouldntHaveOrderCorrectorRole() {
        User standardUser = userFactory.standardUser("test", "test");

        standardUser.getRole(OrderCorrectorRole.class);
    }

    @Test
    public void persistsUsers() {
        User user = userFactory.standardUser("a", "a");
        template.executeInTx((em) -> {
            em.persist(user);
        });

        template.executeInTx((em) -> {
            User u = (User) em.createQuery("FROM UserCore").getResultList().get(0);
            u.addRole(new InvoiceCorrectorRole());
            u.addRole(new OrderCorrectorRole());
        });

        template.executeInTx((em) -> {
            User u = (User) em.createQuery("FROM UserCore").getResultList().get(0);
            assertThat(u.getRole(InvoiceCorrectorRole.class)).isNotNull();
            assertThat(u.getRole(OrderCorrectorRole.class)).isNotNull();
        });
    }

}
