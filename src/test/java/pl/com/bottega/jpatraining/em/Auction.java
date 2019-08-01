package pl.com.bottega.jpatraining.em;

import javax.persistence.Access;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "auction_em")
public class Auction {

    @Id
    private Long id;

    private String name;

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
