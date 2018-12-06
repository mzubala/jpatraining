package pl.com.bottega.jpatraining.onetoone;

public class Address {

    private Long id;

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
