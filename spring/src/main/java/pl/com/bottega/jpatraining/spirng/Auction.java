package pl.com.bottega.jpatraining.spirng;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import org.springframework.data.domain.Persistable;

@Entity
@NamedQuery(
    name = "getAll",
    query = "SELECT a FROM Auction a"
)
class Auction implements Persistable<String> {

    @Id
    private String number;

    public Auction(String number) {
        this.number = number;
    }

    public Auction() {
    }


    @Override
    public String getId() {
        return number;
    }

    @Override
    public boolean isNew() {
        return true;
    }
}
