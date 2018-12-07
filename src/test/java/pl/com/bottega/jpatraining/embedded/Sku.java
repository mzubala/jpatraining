package pl.com.bottega.jpatraining.embedded;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Sku {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "cents", column = @Column(name = "p_cents")),
        @AttributeOverride(name = "currency", column = @Column(name = "p_currency"))
    })
    Money price;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "cents", column = @Column(name = "dp_cents")),
        @AttributeOverride(name = "currency", column = @Column(name = "dp_currency"))
    })
    Money discountedPrice;
}
