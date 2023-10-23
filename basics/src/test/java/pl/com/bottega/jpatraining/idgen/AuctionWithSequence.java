package pl.com.bottega.jpatraining.idgen;

import jakarta.persistence.*;

@Table(name = "auction_with_seq")
@Entity
@SequenceGenerator(name = "auction_seq", allocationSize = 20)
public class AuctionWithSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "auction_seq")
    private Long id;

    public Long getId() {
        return id;
    }
}
