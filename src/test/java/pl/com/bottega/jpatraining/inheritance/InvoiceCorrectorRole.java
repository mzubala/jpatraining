package pl.com.bottega.jpatraining.inheritance;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.Collection;

@Entity
public class InvoiceCorrectorRole extends UserRole {

    @ElementCollection
    private Collection<String> correctedInvoices = new ArrayList<>();

    public void invoiceCorrected(String nr) {
        correctedInvoices.add(nr);
    }

}
