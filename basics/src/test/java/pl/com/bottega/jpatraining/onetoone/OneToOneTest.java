package pl.com.bottega.jpatraining.onetoone;

import org.junit.jupiter.api.Test;
import pl.com.bottega.jpatraining.BaseJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

public class OneToOneTest extends BaseJpaTest {

    @Test
    public void savesAddressAndCustomer() {
        // given
        Customer customer = new Customer();
        Address address = new Address();
        customer.setAddress(address);
        address.setCustomer(customer);

        // when
        template.executeInTx((em) -> {
            em.persist(address);
            em.persist(customer);
        });
        template.close();

        // then
        template.executeInTx((em) -> {
            Customer customerFetched = em.find(Customer.class, customer.getId());
            Address addressFetched = em.find(Address.class, address.getId());
            assertThat(customerFetched).isNotNull();
            assertThat(addressFetched).isNotNull();
            assertThat(customerFetched.getAddress() == addressFetched).isTrue();
            assertThat(addressFetched.getCustomer() == customerFetched).isTrue();
        });
    }

    @Test
    public void savesAddressAndCustomerCascading() {
        // given
        Customer customer = new Customer();
        Address address = new Address();
        customer.setAddress(address);
        address.setCustomer(customer);

        // when
        template.executeInTx((em) -> {
            em.persist(customer);
        });
        template.close();

        // then
        template.executeInTx((em) -> {
            Customer customerFetched = em.find(Customer.class, customer.getId());
            Address addressFetched = em.find(Address.class, address.getId());
            assertThat(customerFetched).isNotNull();
            assertThat(addressFetched).isNotNull();
            assertThat(customerFetched.getAddress() == addressFetched).isTrue();
            assertThat(addressFetched.getCustomer() == customerFetched).isTrue();
        });
    }

    @Test
    public void deletesCustomerCascading() {
        // given
        Customer customer = new Customer();
        Address address = new Address();
        customer.setAddress(address);
        address.setCustomer(customer);
        template.executeInTx((em) -> {
            em.persist(customer);
        });
        template.close();

        // when
        template.executeInTx((em) -> {
            em.remove(em.getReference(Customer.class, customer.getId()));
        });
        template.close();

        // then
        template.executeInTx((em) -> {
            assertThat(em.find(Address.class, address.getId())).isNull();
        });
    }

    @Test
    public void lazyLoadsAddress() {
        // given
        Customer customer = new Customer();
        Address address = new Address();
        customer.setAddress(address);
        address.setCustomer(customer);
        template.executeInTx((em) -> {
            em.persist(customer);
        });
        template.close();

        // then
        template.executeInTx((em) -> {
            template.getStatistics().clear();
            Customer customerFetched = em.find(Customer.class, customer.getId());
            assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1L);
            assertThat(customerFetched.getAddress()).isInstanceOf(Address.class);
            assertThat(customerFetched.getAddress()).isNotExactlyInstanceOf(Address.class);
            System.out.println(customerFetched.getAddress().getClass());
            assertThat(customerFetched.getAddress().getStreet()).isNotNull();
            assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(2L);
        });
    }

    @Test
    public void lazyLoadingOneToOneFromSlaveSide() {
        // given
        Customer customer = new Customer();
        Address address = new Address();
        customer.setAddress(address);
        address.setCustomer(customer);
        template.executeInTx((em) -> {
            em.persist(customer);
        });
        template.close();
        template.getStatistics().clear();

        // when
        template.executeInTx((em) -> {
            template.getStatistics().clear();
            Address addressFetched = em.find(Address.class, address.getId());
        });
        //assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(??);
    }

}
