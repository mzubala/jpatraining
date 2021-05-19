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

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "cents", column = @Column(name = "price_cents")),
            @AttributeOverride(name = "currency", column = @Column(name = "price_currency"))
    })
    Money price;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "cents", column = @Column(name = "dis_price_cents")),
            @AttributeOverride(name = "currency", column = @Column(name = "dis_price_currency"))
    })
    Money discountedPrice;

}
