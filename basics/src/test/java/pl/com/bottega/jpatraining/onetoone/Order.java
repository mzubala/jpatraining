package pl.com.bottega.jpatraining.onetoone;

import jakarta.persistence.*;

@Entity
@Table(name = "order_o2o")
class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    private Customer customer;

    public Order(Customer customer) {
        this.customer = customer;
    }

    public Order() {
    }
}
