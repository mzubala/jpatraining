package pl.com.bottega.jpatraining.onetoone;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;

@Entity
public class Address {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private Customer customer;

    public String getStreet() {
        return street;
    }

    private String street = "test";

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
}
