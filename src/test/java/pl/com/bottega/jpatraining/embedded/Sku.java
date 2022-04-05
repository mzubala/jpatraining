package pl.com.bottega.jpatraining.embedded;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Sku {

    @Id
    @GeneratedValue
    Long id;

    @AttributeOverrides({
        @AttributeOverride(name = "cents", column = @Column(name = "price_cents")),
        @AttributeOverride(name = "currency", column = @Column(name = "price_currency"))
    })
    @Embedded
    Money price;

    @AttributeOverrides({
        @AttributeOverride(name = "cents", column = @Column(name = "discounted_price_cents")),
        @AttributeOverride(name = "currency", column = @Column(name = "discounted_price_currency"))
    })
    @Embedded
    Money discountedPrice;

}
