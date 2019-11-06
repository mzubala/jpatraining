package pl.com.bottega.jpatraining.onetoone;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "address", fetch = FetchType.LAZY, optional = false)
    private Customer customer;

    public String getStreet() {
        return street;
    }

    String street = "test";

    public Long getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void printStreet() {
        System.out.println(street);
    }
}
