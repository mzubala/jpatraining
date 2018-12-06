package pl.com.bottega.jpatraining.onetoone;

public class Customer {

    private Long id;

    private Address address;

    public Address getAddress() {
        return address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
