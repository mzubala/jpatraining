package pl.com.bottega.jpatraining.idgen;

import jakarta.persistence.*;

@Table(name = "auction_with_seq")
@SequenceGenerator(name = "auction_seq", initialValue = 1000, allocationSize = 25)
@Entity
public class AuctionWithSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "auction_seq")
    private Long id;

    public Long getId() {
        return id;
    }
}
