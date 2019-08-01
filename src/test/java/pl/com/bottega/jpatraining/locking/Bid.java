package pl.com.bottega.jpatraining.locking;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

@Entity
public class Bid {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private AuctionWithLocking auctionWithLocking;

    private BigDecimal value;

    public Bid(AuctionWithLocking auctionWithLocking, BigDecimal value) {
        this.auctionWithLocking = auctionWithLocking;
        this.value = value;
    }
}
