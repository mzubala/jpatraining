package pl.com.bottega.jpatraining.onetoone;

import jakarta.persistence.*;

@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
            mappedBy = "customer",
            orphanRemoval = true,
            fetch = FetchType.LAZY,
            optional = false
    )
    @PrimaryKeyJoinColumn
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
