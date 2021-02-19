package pl.com.bottega.jpatraining.idgen;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Table(name = "auction_with_seq")
@Entity
@SequenceGenerator(
     name = "auction_seq",
     allocationSize = 30
)
public class AuctionWithSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "auction_seq")
    private Long id;

    public Long getId() {
        return id;
    }
}
