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
    public void removesOrphanedAddress() {
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
            var customerFetched = em.find(Customer.class, customer.getId());
            customerFetched.setAddress(null);
        });
        template.close();

        // then
        template.executeInTx((em) -> {
            assertThat(em.find(Customer.class, customer.getId()).getAddress()).isNull();
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
            assertThat(addressFetched.getCustomer()).isNotExactlyInstanceOf(Customer.class);
        });
    }

    @Test
    public void np1SelectProblemWithLazyLoading() {
        // given
        int n = 100;
        for (int i = 0; i < n; i++) {
            createTestCustomer();
        }
        template.close();
        template.getStatistics().clear();

        // when
        var customers = template.getEntityManager().createQuery("SELECT c FROM Customer c", Customer.class)
            .getResultList(); // 1 query
        for (var customer : customers) {
            System.out.println(customer.getAddress().toString()); // 1 query
        } // * n

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(n + 1);
    }

    @Test
    public void np1SelectProblemWithLazyLoadingSolution() {
        // given
        int n = 100;
        for (int i = 0; i < n; i++) {
            createTestCustomer();
        }
        template.close();
        template.getStatistics().clear();

        // when
        var customers = template.getEntityManager().createQuery("SELECT c FROM Customer c LEFT JOIN FETCH c.address", Customer.class)
            .getResultList(); // 1 query
        for (var customer : customers) {
            System.out.println(customer.getAddress().toString()); // 0 queries
        } // * n

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1);
    }

    @Test
    public void np1SelectProblemWithEagerLoading() {
        // given
        int n = 100;
        for (int i = 0; i < n; i++) {
            var customer = createTestCustomer();
            template.executeInTx(em -> {
                em.persist(new Order(customer));
            });
        }
        template.close();
        template.getStatistics().clear();

        // when
        template.getEntityManager().createQuery("SELECT o FROM Order o", Order.class)
            .getResultList(); // 1 query + n queries

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(n + 1);
    }

    @Test
    public void np1SelectProblemWithEagerLoadingSolution() {
        // given
        int n = 100;
        for (int i = 0; i < n; i++) {
            var customer = createTestCustomer();
            template.executeInTx(em -> {
                em.persist(new Order(customer));
            });
        }
        template.close();
        template.getStatistics().clear();

        // when
        template.getEntityManager().createQuery("SELECT o FROM Order o JOIN FETCH o.customer", Order.class)
            .getResultList(); // 1 query

        // then
        assertThat(template.getStatistics().getPrepareStatementCount()).isEqualTo(1);
    }

    private Customer createTestCustomer() {
        return template.executeInTx((em) -> {
            var customer = new Customer();
            var address = new Address();
            customer.setAddress(address);
            address.setCustomer(customer);
            em.persist(customer);
            return customer;
        });
    }

}
