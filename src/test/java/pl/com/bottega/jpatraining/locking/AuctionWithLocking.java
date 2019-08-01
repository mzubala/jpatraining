package pl.com.bottega.jpatraining.locking;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedList;

@Entity
public class AuctionWithLocking {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "auctionWithLocking")
    private Collection<Bid> bids = new LinkedList<>();

    @Version
    private Long version;

    public void placeBid(BigDecimal value) {

        this.bids.add(new Bid(this, value));
    }

    public Long getId() {
        return id;
    }

    public Long getVersion() {
        return version;
    }
}
