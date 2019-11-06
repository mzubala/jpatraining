package pl.com.bottega.jpatraining.idgen;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "auction_seq_opti")
public class AuctionWithSequenceOptimized {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "auction_seq_opti_gen")
    @SequenceGenerator(name = "auction_seq_opti_gen")
    private Long id;


    public Long getId() {
        return id;
    }
}
