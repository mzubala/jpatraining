package pl.com.bottega.jpatraining.inheritance;


import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.Collection;

@Entity
public class OrderCorrectorRole extends UserRole {

    @ElementCollection
    private Collection<String> correctedOrders = new ArrayList<>();

    public void orderCorrected(String nr) {
        correctedOrders.add(nr);
    }

}
