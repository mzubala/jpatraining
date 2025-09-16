package pl.com.bottega.jpatraining.embedded;

import jakarta.persistence.*;

@Entity
public class Sku {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(
            name = "cents",
            column = @Column(name = "price_cents")
        ),
        @AttributeOverride(
            name = "currency",
            column = @Column(name = "price_currency")
        )
    })
    Money price;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(
            name = "cents",
            column = @Column(name = "discounted_cents")
        ),
        @AttributeOverride(
            name = "currency",
            column = @Column(name = "discounted_currency")
        )
    })
    Money discountedPrice;

}
