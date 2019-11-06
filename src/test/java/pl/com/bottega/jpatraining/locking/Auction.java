package pl.com.bottega.jpatraining.locking;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Version;
import java.util.List;

@Entity(name = "AuctionWithLocking")
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "auction", cascade = CascadeType.PERSIST)
    @OrderColumn
    private List<Bid> bids;

    @Version
    private Long version;

    public void placeBid() {
        if(bids.size() == 0) {
            bids.add(new Bid(this, 1L));
        } else {
            Bid lastBid = bids.get(bids.size() - 1);
            bids.add(new Bid(this, lastBid.value + 1));
        }
    }

    public Long getId() {
        return id;
    }

    public Long getVersion() {
        return version;
    }
}

@Entity
class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Bid() {
    }

    public Bid(Auction auction, Long value) {
        this.auction = auction;
        this.value = value;
    }

    @ManyToOne
    Auction auction;

    Long value;

}
