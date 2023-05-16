package pl.com.bottega.jpatraining.embedded;

import jakarta.persistence.AssociationOverride;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;

@Entity
public class Sku {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "cents", column = @Column(name = "price_cents")),
        @AttributeOverride(name = "currency", column = @Column(name = "price_currency"))
    })
    Money price;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "cents", column = @Column(name = "discounted_price_cents")),
        @AttributeOverride(name = "currency", column = @Column(name = "discounted_price_currency"))
    })
    Money discountedPrice;

}
