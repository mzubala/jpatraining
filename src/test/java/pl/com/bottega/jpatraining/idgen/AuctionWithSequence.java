package pl.com.bottega.jpatraining.idgen;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Table(name = "auction_with_seq")
@Entity
public class AuctionWithSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "my_generator")
    @SequenceGenerator(name = "my_generator", sequenceName = "my_sequence", allocationSize = 25)
    private Long id;

    public Long getId() {
        return id;
    }
}
