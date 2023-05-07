package pl.com.bottega.jpatraining.read;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;

@Entity
public class CarAd {

    @Id
    Long id;

    @ManyToOne
    Model model;

    Integer productionYear;
    Integer firstRegistrationYear;

    boolean damaged;
    boolean firstOwner;

    @Enumerated(EnumType.ORDINAL)
    Fuel fuel;

    BigDecimal price;

    @Embedded
    Engine engine;

    public CarAd(int id, Model model, int productionYear, Integer firstRegistrationYear, boolean damaged, boolean firstOwner, Fuel fuel, BigDecimal price) {
        this.id = (long) id;
        this.model = model;
        this.productionYear = productionYear;
        this.firstRegistrationYear = firstRegistrationYear;
        this.damaged = damaged;
        this.firstOwner = firstOwner;
        this.fuel = fuel;
        this.price = price;
    }

    public CarAd() {
    }
}
